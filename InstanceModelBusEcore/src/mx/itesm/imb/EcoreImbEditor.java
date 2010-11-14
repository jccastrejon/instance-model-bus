package mx.itesm.imb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * 
 * @author jccastrejon
 * 
 */
public class EcoreImbEditor {

    /**
     * 
     */
    private static Template aspectTemplate;

    /**
     * 
     */
    private static Template controllerTemplate;

    /**
     * 
     */
    private static boolean velocityInit = false;

    static {
        EcoreImbEditor.aspectTemplate = EcoreImbEditor.getEcoreTemplate();
        EcoreImbEditor.controllerTemplate = EcoreImbEditor.getControllerTemplate();
    }

    public static void main(String[] args) {
        // 0: ecoreProject, 1: busProject, 2: templateProject
        EcoreImbEditor.configureRestTemplate(new File(args[0]), new File(args[1]), new File(args[2]));
        EcoreImbEditor.createRooApplication(new File(args[0]), new File(args[1]), new File(args[2]));
        EcoreImbEditor.generateProvidersArtifacts(new File(args[0]));
    }

    /**
     * 
     * @param ecoreProject
     */
    @SuppressWarnings("unchecked")
    public static void generateProvidersArtifacts(final File ecoreProject) {
        File provider;
        File imbProject;
        List<File> imbTypes;
        Iterator<File> files;

        // Get imb types
        imbProject = new File(ecoreProject.getParent(), ecoreProject.getName() + ".edit");
        files = FileUtils.iterateFiles(new File(imbProject, "/src/imb"), new String[] { "java" }, true);
        imbTypes = new ArrayList<File>();
        while (files.hasNext()) {
            imbTypes.add(files.next());
        }

        // Get providers files
        files = FileUtils.iterateFiles(imbProject, new IOFileFilter() {

            @Override
            public boolean accept(final File file) {
                return file.getName().endsWith("ItemProvider.java");
            }

            @Override
            public boolean accept(File directory, String file) {
                return file.endsWith("ItemProvider.java");
            }
        }, TrueFileFilter.INSTANCE);

        while (files.hasNext()) {
            try {
                provider = files.next();
                EcoreImbEditor.writeEcoreAspect(provider, imbTypes);
                EcoreImbEditor.writeEcoreController(imbProject, provider, imbTypes);
                System.out.println("Artifacts for " + provider + " successfully generated");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error generating Artifacts: " + e.getMessage());
            }
        }
    }

    /**
     * 
     * @param templateProject
     */
    @SuppressWarnings("unchecked")
    public static void createRooApplication(final File ecoreProject, final File busProject, final File templateProject) {
        File imbProject;
        String pluginContent;
        String tomcatConfiguration;
        Collection<File> pluginFiles;

        try {
            imbProject = new File(ecoreProject.getParent(), ecoreProject.getName() + ".edit/imb/");
            FileUtils.deleteDirectory(imbProject);

            // Create the roo application
            FileUtils
                    .copyFile(new File(templateProject, "/templates/install.roo"), new File(imbProject, "install.roo"));
            EcoreImbEditor.executeCommand("roo script --file install.roo", imbProject);

            // IMB types configuration
            FileUtils.copyDirectory(new File(busProject, "/src/main/java/imb"), new File(ecoreProject.getParent(),
                    ecoreProject.getName() + ".edit/imb/src/main/java/imb"));
            FileUtils.copyFile(new File(busProject, "/src/main/resources/schema.xsd"),
                    new File(ecoreProject.getParent(), ecoreProject.getName()
                            + ".edit/imb/src/main/resources/schema.xsd"));

            FileUtils.copyFile(new File(busProject,
                    "/src/main/resources/META-INF/spring/applicationContext-contentresolver.xml"), new File(
                    ecoreProject.getParent(), ecoreProject.getName()
                            + ".edit/imb/src/main/resources/META-INF/spring/applicationContext-contentresolver.xml"));

            // Update the plugin configuration
            pluginFiles = FileUtils.listFiles(ecoreProject, new IOFileFilter() {
                @Override
                public boolean accept(File file) {
                    return (file.getName().endsWith("Plugin.java"));
                }

                @Override
                public boolean accept(File dir, String file) {
                    return (file.endsWith("Plugin.java"));
                }
            }, new IOFileFilter() {
                @Override
                public boolean accept(File file) {
                    return (file.getName().endsWith("Plugin.java"));
                }

                @Override
                public boolean accept(File dir, String file) {
                    return (file.endsWith("Plugin.java"));
                }
            });
            for (File plugin : pluginFiles) {
                pluginContent = FileUtils.readFileToString(plugin);
                pluginContent = pluginContent.substring(0,
                        pluginContent.indexOf("public static class Implementation extends EclipsePlugin"));

                // Tomcat configuration
                tomcatConfiguration = FileUtils.readFileToString(new File(templateProject, "/templates/Plugin.txt"));
                tomcatConfiguration = tomcatConfiguration.replace("${imbProject}", imbProject.getPath());

                FileUtils.writeStringToFile(plugin, pluginContent + tomcatConfiguration);
                break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while configuring Roo application: " + e.getMessage());
        }
    }

    /**
     * 
     * @param ecoreProject
     * @param busProject
     * @param templateProject
     */
    @SuppressWarnings("unchecked")
    public static void configureRestTemplate(final File ecoreProject, final File busProject, final File templateProject) {
        File manifestFile;
        String configuration;
        String exportPackage;
        String bundleActivator;
        String templateContent;
        String oxmConfiguration;

        try {
            // Copy lib
            FileUtils.copyDirectory(new File(templateProject, "/lib.rest"), new File(ecoreProject.getParent(),
                    ecoreProject.getName() + ".edit/lib"));

            // Add rest configuration
            templateContent = FileUtils.readFileToString(new File(templateProject, "/templates/beans.xml"));
            configuration = FileUtils.readFileToString(new File(busProject,
                    "/src/main/resources/META-INF/spring/applicationContext-contentresolver.xml"));
            oxmConfiguration = configuration.substring(configuration.indexOf("<oxm:"),
                    configuration.indexOf("</oxm:jaxb2-marshaller>") + "</oxm:jaxb2-marshaller>".length());
            templateContent = templateContent.replace("<!-- oxm -->", oxmConfiguration);
            FileUtils.writeStringToFile(new File(ecoreProject.getParent(), ecoreProject.getName() + ".edit/beans.xml"),
                    templateContent);

            // Copy imb types
            FileUtils.copyDirectory(new File(busProject, "/src/main/java/imb"), new File(ecoreProject.getParent(),
                    ecoreProject.getName() + ".edit/src/imb"));

            // Update classpath
            FileUtils.copyFile(new File(templateProject, "templates/classpath.xml"), new File(ecoreProject.getParent(),
                    ecoreProject.getName() + ".edit/.classpath"));
            templateContent = FileUtils.readFileToString(new File(templateProject, "/templates/project.xml"));
            FileUtils.writeStringToFile(new File(ecoreProject.getParent(), ecoreProject.getName() + ".edit/.project"),
                    templateContent.replace("${projectName}", ecoreProject.getName()));

            // Update Manifest
            bundleActivator = null;
            exportPackage = null;
            manifestFile = new File(ecoreProject.getParent(), ecoreProject.getName() + ".edit/META-INF/MANIFEST.MF");
            for (String line : (List<String>) FileUtils.readLines(manifestFile)) {
                if (line.startsWith("Bundle-Activator")) {
                    bundleActivator = line.replace("Bundle-Activator: ", "");
                } else if (line.startsWith("Export-Package: ")) {
                    exportPackage = line.replace("Export-Package: ", "");
                }
            }
            templateContent = FileUtils.readFileToString(new File(templateProject, "/templates/manifest.MF"));
            templateContent = templateContent.replaceAll("\\$\\{projectName\\}", ecoreProject.getName());
            templateContent = templateContent.replace("${bundleActivator}", bundleActivator);
            templateContent = templateContent.replace("${exportPackage}", exportPackage);
            FileUtils.writeStringToFile(manifestFile, templateContent);
        } catch (Exception e) {
            System.out.println("Error while configuring Rest template: " + e.getMessage());
        }
    }

    /**
     * 
     * @param ecoreProject
     * @param provider
     * @param imbTypes
     * @throws IOException
     */
    private static void writeEcoreController(final File imbProject, final File provider, final List<File> imbTypes)
            throws IOException {
        String type;
        Writer writer;
        boolean typeFound;
        String packageName;
        VelocityContext context;

        context = new VelocityContext();
        type = provider.getName().replace("ItemProvider.java", "");
        packageName = provider
                .getPath()
                .substring(provider.getPath().indexOf("src") + "src".length() + 1, provider.getPath().indexOf(type) - 1)
                .replace('/', '.');
        typeFound = false;
        for (File imbType : imbTypes) {
            if (imbType.getName().equals(type + ".java")) {
                typeFound = true;
                context.put(
                        "imbTypePackage",
                        imbType.getPath()
                                .substring(imbType.getPath().indexOf("src") + "src".length() + 1,
                                        imbType.getPath().indexOf(type) - 1).replace(File.separatorChar, '.'));
                break;
            }
        }

        if (typeFound) {
            context.put("type", type);
            context.put("packageName", packageName);
            writer = new FileWriter(new File(imbProject, "/imb/src/main/java/mx/itesm/ecore/web/EcoreController" + type
                    + ".aj"));
            EcoreImbEditor.controllerTemplate.merge(context, writer);
            writer.close();
        }
    }

    /**
     * 
     * @param provider
     * @param imbTypes
     * @throws IOException
     */
    private static void writeEcoreAspect(final File provider, final List<File> imbTypes) throws IOException {
        Writer writer;
        String typeName;
        String packageName;
        VelocityContext context;

        typeName = provider.getName().replace(".java", "");
        packageName = provider.getPath()
                .substring(provider.getPath().indexOf("src") + 4, provider.getPath().indexOf(typeName) - 1)
                .replace('/', '.');

        context = new VelocityContext();
        context.put("typeName", typeName);
        context.put("packageName", packageName);
        context.put("typePackage", packageName.split("\\.")[0]);
        context.put("imbAddress", "http://localhost:9090/tlbus-0.1.0.BUILD-SNAPSHOT");

        for (File imbType : imbTypes) {
            if (imbType.getName().equals(typeName.replace("ItemProvider", "") + ".java")) {
                context.put("generateHelperMethod", true);
                context.put(
                        "imbTypePackage",
                        imbType.getPath()
                                .substring(imbType.getPath().indexOf("src") + "src".length() + 1,
                                        imbType.getPath().indexOf(typeName.replace("ItemProvider", "")) - 1)
                                .replace(File.separatorChar, '.'));
                break;
            }
        }

        writer = new FileWriter(new File(provider.getParentFile(), "EcoreAspect_" + typeName + ".aj"));
        EcoreImbEditor.aspectTemplate.merge(context, writer);
        writer.close();
    }

    /**
     * 
     * @return
     */
    private static Template getEcoreTemplate() {
        Template returnValue;

        try {
            EcoreImbEditor.initVelocity();
            returnValue = Velocity.getTemplate("mx/itesm/imb/EcoreAspect.vml");
        } catch (Exception e) {
            returnValue = null;
        }

        return returnValue;
    }

    /**
     * 
     * @return
     */
    private static Template getControllerTemplate() {
        Template returnValue;

        try {
            EcoreImbEditor.initVelocity();
            returnValue = Velocity.getTemplate("mx/itesm/imb/EcoreController.vml");
        } catch (Exception e) {
            returnValue = null;
        }

        return returnValue;
    }

    /**
     * 
     */
    private static void initVelocity() {
        Properties velocityProperties;
        try {
            if (!velocityInit) {
                velocityProperties = new Properties();
                velocityProperties.put("resource.loader", "class");
                velocityProperties.put("class.resource.loader.class",
                        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
                Velocity.init(velocityProperties);
                velocityInit = true;
            }
        } catch (Exception e) {
            System.out.println("Error configuring velocity: " + e.getMessage());
        }
    }

    /**
     * 
     * @param command
     * @param baseDir
     * @return
     */
    private static void executeCommand(final String command, final File baseDir) throws Exception {
        Process process;
        int processCode;

        process = Runtime.getRuntime().exec(command, null, baseDir);
        processCode = process.waitFor();
        if (processCode != 0) {
            throw new RuntimeException("Unable to execute: " + command + ", in: " + baseDir);
        }
    }
}

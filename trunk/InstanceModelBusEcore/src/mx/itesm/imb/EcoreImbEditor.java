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
    private static Template selectionTemplate;

    /**
     * 
     */
    private static boolean velocityInit = false;

    static {
        EcoreImbEditor.aspectTemplate = EcoreImbEditor.getEcoreTemplate();
        EcoreImbEditor.controllerTemplate = EcoreImbEditor.getControllerTemplate();
        EcoreImbEditor.selectionTemplate = EcoreImbEditor.getSelectionTemplate();
    }

    public static void main(String[] args) {
        // 0: ecoreProject, 1: busProject, 2: templateProject
        EcoreImbEditor.configureRestTemplate(new File(args[0]), new File(args[1]), new File(args[2]));
        EcoreImbEditor.createRooApplication(new File(args[0]), new File(args[1]), new File(args[2]));
        EcoreImbEditor.generateProvidersArtifacts(new File(args[0]), new File(args[2]));
    }

    /**
     * 
     * @param ecoreProject
     */
    @SuppressWarnings("unchecked")
    public static void generateProvidersArtifacts(final File ecoreProject, final File templateProject) {
        File provider;
        File imbProject;
        List<File> imbTypes;
        Iterator<File> files;
        List<String> types;
        String typesPackage;

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

        typesPackage = null;
        types = new ArrayList<String>();
        while (files.hasNext()) {
            try {
                provider = files.next();
                types.add(provider.getName().replace("ItemProvider.java", ""));
                typesPackage = provider
                        .getPath()
                        .substring(provider.getPath().indexOf("src") + "src".length() + 1,
                                provider.getPath().indexOf(provider.getName().replace(".java", "")) - 1)
                        .replace('/', '.');

                EcoreImbEditor.writeEcoreAspect(provider, imbTypes);
                EcoreImbEditor.writeEcoreController(imbProject, templateProject, provider, imbTypes);
                System.out.println("Artifacts for " + provider + " successfully generated");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error generating Artifacts: " + e.getMessage());
            }
        }

        // Configuration properties file
        try {
            FileUtils.copyFile(new File(templateProject, "/templates/configuration-template.properties"), new File(
                    imbProject, "/src/mx/itesm/imb/configuration.properties"));
        } catch (IOException e) {
            System.out.println("Unable to generate configuration properties file: " + e.getMessage());
        }

        // Selection aspect
        typesPackage = typesPackage.replace(".provider", "");
        EcoreImbEditor.writeSelectionAspect(ecoreProject, typesPackage, types);
    }

    /**
     * 
     * @param templateProject
     */
    @SuppressWarnings("unchecked")
    public static void createRooApplication(final File ecoreProject, final File busProject, final File templateProject) {
        File imbProject;
        File editProject;
        String pluginContent;
        String pomContent;
        String tomcatConfiguration;
        Collection<File> pluginFiles;

        try {
            editProject = new File(ecoreProject.getParent(), ecoreProject.getName() + ".edit");
            imbProject = new File(editProject, "/imb/");
            FileUtils.deleteDirectory(imbProject);

            // Create the roo application
            FileUtils
                    .copyFile(new File(templateProject, "/templates/install.roo"), new File(imbProject, "install.roo"));
            EcoreImbEditor.executeCommand("roo script --file install.roo", imbProject);

            // Update libraries
            pomContent = FileUtils.readFileToString(new File(imbProject, "pom.xml"));
            pomContent = pomContent.replaceFirst("</dependencies>",
                    FileUtils.readFileToString(new File(templateProject, "/templates/pom.xml")));
            FileUtils.writeStringToFile(new File(imbProject, "pom.xml"), pomContent);

            // IMB types configuration
            FileUtils.copyDirectory(new File(busProject, "/src/main/java/imb"), new File(imbProject,
                    "/src/main/java/imb"));
            FileUtils.copyFile(new File(busProject, "/src/main/resources/schema.xsd"), new File(imbProject,
                    "/src/main/resources/schema.xsd"));

            FileUtils.copyFile(new File(busProject,
                    "/src/main/resources/META-INF/spring/applicationContext-contentresolver.xml"), new File(imbProject,
                    "/src/main/resources/META-INF/spring/applicationContext-contentresolver.xml"));

            // Update the plugin configuration
            pluginFiles = FileUtils.listFiles(new File(editProject, "/src"), new IOFileFilter() {
                @Override
                public boolean accept(File file) {
                    return (file.getName().endsWith("Plugin.java"));
                }

                @Override
                public boolean accept(File dir, String file) {
                    return (file.endsWith("Plugin.java"));
                }
            }, TrueFileFilter.INSTANCE);
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
            FileUtils.copyDirectory(new File(templateProject, "/lib.rest"), new File(ecoreProject.getParent(),
                    ecoreProject.getName() + ".editor/lib"));
            FileUtils.copyDirectory(new File(templateProject, "/lib.jdom"), new File(ecoreProject.getParent(),
                    ecoreProject.getName() + ".editor/lib"));

            // Add rest configuration
            templateContent = FileUtils.readFileToString(new File(templateProject, "/templates/beans.xml"));
            configuration = FileUtils.readFileToString(new File(busProject,
                    "/src/main/resources/META-INF/spring/applicationContext-contentresolver.xml"));
            oxmConfiguration = configuration.substring(configuration.indexOf("<oxm:"),
                    configuration.indexOf("</oxm:jaxb2-marshaller>") + "</oxm:jaxb2-marshaller>".length());
            templateContent = templateContent.replace("<!-- oxm -->", oxmConfiguration);
            FileUtils.writeStringToFile(new File(ecoreProject.getParent(), ecoreProject.getName() + ".edit/beans.xml"),
                    templateContent);
            FileUtils.writeStringToFile(
                    new File(ecoreProject.getParent(), ecoreProject.getName() + ".editor/beans.xml"), templateContent);

            // Copy imb types
            FileUtils.copyDirectory(new File(busProject, "/src/main/java/imb"), new File(ecoreProject.getParent(),
                    ecoreProject.getName() + ".edit/src/imb"));
            FileUtils.copyDirectory(new File(busProject, "/src/main/java/imb"), new File(ecoreProject.getParent(),
                    ecoreProject.getName() + ".editor/src/imb"));

            // Update classpath
            FileUtils.copyFile(new File(templateProject, "templates/classpath.xml"), new File(ecoreProject.getParent(),
                    ecoreProject.getName() + ".edit/.classpath"));
            FileUtils.copyFile(new File(templateProject, "templates/classpath.editor.xml"),
                    new File(ecoreProject.getParent(), ecoreProject.getName() + ".editor/.classpath"));
            templateContent = FileUtils.readFileToString(new File(templateProject, "/templates/project.xml"));
            FileUtils.writeStringToFile(new File(ecoreProject.getParent(), ecoreProject.getName() + ".edit/.project"),
                    templateContent.replace("${projectName}", ecoreProject.getName()));
            FileUtils.writeStringToFile(
                    new File(ecoreProject.getParent(), ecoreProject.getName() + ".editor/.project"),
                    templateContent.replace("${projectName}", ecoreProject.getName()));

            // Update Manifest
            bundleActivator = null;
            exportPackage = null;

            // edit project
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

            // editor project
            manifestFile = new File(ecoreProject.getParent(), ecoreProject.getName() + ".editor/META-INF/MANIFEST.MF");
            for (String line : (List<String>) FileUtils.readLines(manifestFile)) {
                if (line.startsWith("Bundle-Activator")) {
                    bundleActivator = line.replace("Bundle-Activator: ", "");
                } else if (line.startsWith("Export-Package: ")) {
                    exportPackage = line.replace("Export-Package: ", "");
                }
            }
            templateContent = FileUtils.readFileToString(new File(templateProject, "/templates/manifest.editor.MF"));
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
     * @param imbProject
     * @param templateProject
     * @param provider
     * @param imbTypes
     * @throws IOException
     */
    private static void writeEcoreController(final File imbProject, final File templateProject, final File provider,
            final List<File> imbTypes) throws IOException {
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
                    + ".java"));
            EcoreImbEditor.controllerTemplate.merge(context, writer);
            writer.close();

            // Configuration properties file
            FileUtils.copyFile(new File(templateProject, "/templates/configuration-template.properties"), new File(
                    imbProject, "/imb/src/main/resources/mx/itesm/imb/configuration.properties"));
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
        context.put("imbAddress", "http://localhost:9090/todolistbus-0.1.0.BUILD-SNAPSHOT");

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

    @SuppressWarnings("unchecked")
    private static void writeSelectionAspect(final File ecoreProject, final String typesPackage,
            final List<String> types) {
        Writer writer;
        String packageName;
        VelocityContext context;
        StringBuilder validTypes;
        Collection<File> contributorFiles;

        try {
            validTypes = new StringBuilder();
            for (String type : types) {
                if (!type.toLowerCase().equals("system")) {
                    validTypes.append("validTypes.add(\"" + type + "\");\n");
                }
            }

            contributorFiles = FileUtils.listFiles(new File(ecoreProject.getParent(), ecoreProject.getName()
                    + ".editor"), new IOFileFilter() {
                @Override
                public boolean accept(File dir, String file) {
                    return file.endsWith("Contributor.java");
                }

                @Override
                public boolean accept(File file) {
                    return file.getName().endsWith("Contributor.java");
                }
            }, TrueFileFilter.INSTANCE);

            for (File contributor : contributorFiles) {
                context = new VelocityContext();
                packageName = contributor
                        .getPath()
                        .substring(contributor.getPath().indexOf("src") + "src".length() + 1,
                                contributor.getPath().indexOf(contributor.getName().replace(".java", "")) - 1)
                        .replace('/', '.');

                context.put("validTypes", validTypes);
                context.put("packageName", packageName);
                context.put("typesPackage", typesPackage);
                context.put("contributor", contributor.getName().replace(".java", ""));

                writer = new FileWriter(new File(contributor.getParentFile(), "SelectionAspect.aj"));
                EcoreImbEditor.selectionTemplate.merge(context, writer);
                writer.close();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable to update selection service: " + e.getMessage());
        }
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
     * @return
     */
    private static Template getSelectionTemplate() {
        Template returnValue;

        try {
            EcoreImbEditor.initVelocity();
            returnValue = Velocity.getTemplate("mx/itesm/imb/SelectionAspect.vml");
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

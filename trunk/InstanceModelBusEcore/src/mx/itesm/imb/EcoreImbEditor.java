package mx.itesm.imb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
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

    static {
        EcoreImbEditor.aspectTemplate = EcoreImbEditor.getEcoreTemplate();
    }

    public static void main(String[] args) {
        // 0: ecoreProject, 1: busProject, 2: templateProject
        EcoreImbEditor.configureRestTemplate(new File(args[0]), new File(args[1]), new File(args[2]));
        EcoreImbEditor.generateProvidersAspects(new File(args[0]));
    }

    /**
     * 
     * @param ecoreProject
     */
    @SuppressWarnings("unchecked")
    public static void generateProvidersAspects(final File ecoreProject) {
        File provider;
        List<File> imbTypes;
        Iterator<File> files;

        // Get imb types
        files = FileUtils.iterateFiles(new File(ecoreProject.getParent(), ecoreProject.getName() + ".edit/src/imb"),
                new String[] { "java" }, true);
        imbTypes = new ArrayList<File>();
        while (files.hasNext()) {
            imbTypes.add(files.next());
        }

        // Get providers files
        files = FileUtils.iterateFiles(new File(ecoreProject.getParent(), ecoreProject.getName() + ".edit"),
                new IOFileFilter() {

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
                System.out.println("Aspect for " + provider + " successfully generated");
            } catch (Exception e) {
                System.out.println("Error generating aspect: " + e.getMessage());
            }
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
        context.put("imbTypes", imbTypes);
        context.put("typeName", typeName);
        context.put("packageName", packageName);
        context.put("typePackage", packageName.split("\\.")[0]);

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
     */
    private static Template getEcoreTemplate() {
        Template returnValue;
        Properties velocityProperties;
        try {
            velocityProperties = new Properties();
            velocityProperties.put("resource.loader", "class");
            velocityProperties.put("class.resource.loader.class",
                    "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            Velocity.init(velocityProperties);
            returnValue = Velocity.getTemplate("mx/itesm/imb/EcoreAspect.vml");
        } catch (Exception e) {
            returnValue = null;
            System.out.println("Error configuring velocity: " + e.getMessage());
        }

        return returnValue;
    }
}

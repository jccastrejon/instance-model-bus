package mx.itesm.imb;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * 
 * @author jccastrejon
 * 
 */
public class ImbBusController {

    /**
     * 
     */
    private static Template controllerTemplate;

    static {
        controllerTemplate = ImbBusController.getEcoreTemplate();
    }

    public static void main(String[] args) {
        ImbBusController.generateImbBusController(new File(args[0]), new File(args[1]));
    }

    @SuppressWarnings("unchecked")
    public static void generateImbBusController(final File rooProject, final File busProject) {
        Writer writer;
        File typeReference;
        File controllerFile;
        int basePackageIndex;
        String imbTypePackage;
        String webConfiguration;
        VelocityContext context;
        Collection<String> types;
        String controllerPackage;
        Iterator<File> typesIterator;

        try {
            // Copy imb types
            FileUtils.copyDirectory(new File(rooProject, "/src/main/java/imb"), new File(busProject,
                    "/src/main/java/imb"));
            FileUtils.copyFile(new File(rooProject, "/src/main/resources/schema.xsd"), new File(busProject,
                    "/src/main/resources/schema.xsd"));

            imbTypePackage = null;
            types = new ArrayList<String>();
            typesIterator = FileUtils.iterateFiles(new File(busProject, "/src/main/java/imb"), new String[] { "java" },
                    true);
            while (typesIterator.hasNext()) {
                typeReference = typesIterator.next();
                if ((!typeReference.getName().equals("ObjectFactory.java"))
                        && (!typeReference.getName().equals("package-info.java"))) {
                    if (FileUtils.readFileToString(typeReference).contains("public class")) {
                        types.add(typeReference.getName().replace(".java", ""));
                        if (imbTypePackage == null) {
                            imbTypePackage = typeReference
                                    .getPath()
                                    .substring(
                                            typeReference.getPath().indexOf("src/main/java") + "src/main/java".length()
                                                    + 1, typeReference.getPath().indexOf(typeReference.getName()) - 1)
                                    .replace(File.separatorChar, '.');
                        }
                    }
                }
            }

            // Add rest configuration
            FileUtils.copyFile(new File(rooProject,
                    "/src/main/resources/META-INF/spring/applicationContext-contentresolver.xml"), new File(busProject,
                    "/src/main/resources/META-INF/spring/applicationContext-contentresolver.xml"));

            context = new VelocityContext();
            context.put("types", types);
            context.put("imbTypePackage", imbTypePackage);

            webConfiguration = FileUtils.readFileToString(new File(busProject,
                    "src/main/webapp/WEB-INF/spring/webmvc-config.xml"));
            basePackageIndex = webConfiguration.indexOf("base-package=\"") + "base-package=\"".length();
            controllerPackage = webConfiguration.substring(basePackageIndex,
                    webConfiguration.indexOf('"', basePackageIndex))
                    + ".web";
            context.put("controllerPackage", controllerPackage);
            context.put("typePackage", controllerPackage.replace(".web", ".domain"));
            controllerFile = new File(busProject, "/src/main/java/" + controllerPackage.replace('.', '/')
                    + "/ImbBusController.java");
            writer = new FileWriter(controllerFile);
            ImbBusController.controllerTemplate.merge(context, writer);
            writer.close();
        } catch (Exception e) {
            System.out.println("Error while configuring IMB Bus: " + e.getMessage());
        }
    }

    private static Template getEcoreTemplate() {
        Template returnValue;
        Properties velocityProperties;
        try {
            velocityProperties = new Properties();
            velocityProperties.put("resource.loader", "class");
            velocityProperties.put("class.resource.loader.class",
                    "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            Velocity.init(velocityProperties);
            returnValue = Velocity.getTemplate("mx/itesm/imb/ImbBusController.vml");
        } catch (Exception e) {
            returnValue = null;
            System.out.println("Error configuring velocity: " + e.getMessage());
        }

        return returnValue;
    }
}

package mx.itesm.imb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.roo.file.monitor.event.FileDetails;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.ProjectMetadata;

/**
 * Implementation of IMB commands that are available via the Roo shell.
 */
@Component
@Service
public class ImbOperationsImpl implements ImbOperations {
    private static Logger logger = Logger.getLogger(ImbOperations.class.getName());

    @Reference
    private MetadataService metadataService;

    @Reference
    private PathResolver pathResolver;

    @Reference
    private FileManager fileManager;

    /**
     * ImbAspect velocity template.
     */
    private static Template aspectTemplate;

    /**
     * JAXB schemagen ant build file.
     */
    private static File schemagenBuildFile;

    static {
        try {
            ImbOperationsImpl.aspectTemplate = ImbOperationsImpl.getVelocityTemplate();
            ImbOperationsImpl.schemagenBuildFile = new File(
                    "/Users/jccastrejon/java/workspace_AgoDic2010/InstanceModelBus/src/main/resources/schemagen.xml");
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    public static Template getVelocityTemplate() throws Exception {
        Template returnValue;
        Properties velocityProperties;

        // TODO: Change to class-path search:.
        velocityProperties = new Properties();
        velocityProperties.put("resource.loader", "file");
        velocityProperties.put("file.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        velocityProperties.put("file.resource.loader.path",
                "/Users/jccastrejon/java/workspace_AgoDic2010/InstanceModelBus/src/main/resources");
        velocityProperties.put("file.resource.loader.cache", "true");
        Velocity.init(velocityProperties);

        returnValue = Velocity.getTemplate("ImbAspect.vml");
        return returnValue;
    }

    public boolean isUpdateControllersAvailable() {
        return metadataService.get(ProjectMetadata.getProjectIdentifier()) != null;
    }

    /**
     * @see mx.itesm.imb.ImbCommands#updateControllers()
     */
    public void updateControllers() {
        String antPath;
        String packageName;
        File controllerFile;
        FileDetails srcRoot;
        File configurationFile;
        String configurationContents;
        SortedSet<FileDetails> entries;

        // Identify mvc-controllers
        antPath = pathResolver.getRoot(Path.SRC_MAIN_JAVA) + File.separatorChar + "**" + File.separatorChar
                + "*Controller.java";
        srcRoot = new FileDetails(new File(pathResolver.getRoot(Path.SRC_MAIN_JAVA)), null);
        entries = fileManager.findMatchingAntPath(antPath);

        // Update mvc-controller's methods to allow communication with the IMB
        for (FileDetails file : entries) {
            try {
                controllerFile = file.getFile();
                packageName = srcRoot.getRelativeSegment(file.getCanonicalPath()).replace(File.separatorChar, '.');
                packageName = packageName.substring(1).substring(0, packageName.lastIndexOf('.') - 1); // java
                packageName = packageName.substring(0, packageName.lastIndexOf('.')); // className
                this.writeImbAspect(controllerFile.getName().substring(0, controllerFile.getName().indexOf(".java")),
                        packageName, controllerFile.getParentFile().getAbsolutePath());
                logger.log(Level.INFO, file.getCanonicalPath() + " controller updated");
            } catch (Exception e) {
                logger.log(Level.SEVERE,
                        "Error while updating " + file.getCanonicalPath() + " controller: " + e.getMessage());
            }
        }

        // Update Rest configuration
        try {
            configurationFile = new File(pathResolver.getRoot(Path.SRC_MAIN_RESOURCES)
                    + "/META-INF/spring/applicationContext-contentresolver.xml");
            configurationContents = FileUtils.readFileToString(configurationFile);

            // TODO: Change to classpath search
            configurationContents = configurationContents.replace(
                    "</beans>",
                    FileUtils.readFileToString(new File(
                            "/Users/jccastrejon/java/workspace_AgoDic2010/InstanceModelBus/src/main/resources",
                            "RestTemplate.vml"))
                            + "</beans>");
            FileUtils.writeStringToFile(configurationFile, configurationContents);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating Rest configuration: " + e.getMessage());
        }
    }

    /**
     * @see mx.itesm.imb.ImbCommands#generateEntitiesSchemas()
     */
    @SuppressWarnings("unchecked")
    public void generateEntitiesSchemas() {
        String tns;
        int tnsIndex;
        File srcFile;
        String antPath;
        int processCode;
        Process process;
        File schemaFile;
        File schemasDir;
        String entityName;
        String packageName;
        File schemagenFile;
        int rootElementIndex;
        String rootElementName;
        List<String> enumNames;
        String schemagenContents;
        String schemaFileContents;
        List<String> outputContents;
        SortedSet<FileDetails> entries;
        List<String> typesSchemaContents;
        List<String> detailsSchemaContents;

        try {
            // Identify the classes that should generate schemas
            antPath = pathResolver.getRoot(Path.SRC_MAIN_JAVA) + File.separatorChar + "**" + File.separatorChar
                    + "*_Roo_JavaBean.aj";
            entries = fileManager.findMatchingAntPath(antPath);

            // Clean directory where the schemas will be generated
            schemasDir = new File(pathResolver.getRoot(Path.SRC_MAIN_JAVA) + "/schema_temp");

            // Generate Java Beans for the identified schemas
            for (FileDetails file : entries) {
                srcFile = file.getFile();
                entityName = srcFile.getName().replace("_Roo_JavaBean.aj", "");
                outputContents = new ArrayList<String>();

                // Format file
                packageName = "";
                for (String line : (List<String>) FileUtils.readLines(srcFile)) {
                    // Remove annotations
                    if (!line.contains("@")) {
                        // Change form aspect to class declaration
                        if (line.startsWith("privileged")) {
                            outputContents.add("@javax.xml.bind.annotation.XmlRootElement(namespace =\"http://"
                                    + packageName + "\")\n");
                            outputContents.add(line.replace("privileged", "public").replace("aspect", "class")
                                    .replace("_Roo_JavaBean", ""));
                        } else {
                            // Remove aspect details
                            outputContents.add(line.replace(entityName + ".", ""));
                        }
                    }

                    if (line.startsWith("package")) {
                        packageName = line.replace("package", "").replace(";", "").trim() + ".imb";
                    }
                }

                // Write file
                FileUtils.writeLines(new File(schemasDir, entityName + ".java"), outputContents);
                logger.log(Level.INFO, "Generating XML schema for " + srcFile);
            }

            // Copy enum types to avoid compilation errors
            antPath = pathResolver.getRoot(Path.SRC_MAIN_JAVA) + File.separatorChar + "**" + File.separatorChar
                    + "*.java";
            entries = fileManager.findMatchingAntPath(antPath);
            enumNames = new ArrayList<String>();
            for (FileDetails file : entries) {
                if (FileUtils.readFileToString(file.getFile()).contains("enum")) {
                    enumNames.add(file.getFile().getName().replace(".java", "").toLowerCase());
                    FileUtils.copyFile(file.getFile(), new File(schemasDir, file.getFile().getName()));
                }
            }

            // Execute schemagen
            schemagenFile = new File(schemasDir, "build.xml");
            schemagenContents = FileUtils.readFileToString(ImbOperationsImpl.schemagenBuildFile);
            schemagenContents = schemagenContents.replace("${output.dir}", schemasDir.getAbsolutePath());
            schemagenContents = schemagenContents.replace("${src.dir}", schemasDir.getAbsolutePath());
            FileUtils.writeStringToFile(schemagenFile, schemagenContents);
            process = Runtime.getRuntime().exec("ant -buildfile " + schemagenFile.getAbsolutePath());
            processCode = process.waitFor();

            // Error while executing schemagen
            if (processCode != 0) {
                throw new RuntimeException();
            }

            // Merge schemas and clean up
            typesSchemaContents = FileUtils.readLines(new File(schemasDir, "schema1.xsd"));
            detailsSchemaContents = FileUtils.readLines(new File(schemasDir, "schema2.xsd"));
            outputContents = new ArrayList<String>(typesSchemaContents.size() + detailsSchemaContents.size());

            // Elements
            for (String line : typesSchemaContents) {
                if (!line.contains("<xs:import") && !line.contains("</xs:schema")) {
                    if (line.contains("<xs:schema")) {
                        tnsIndex = line.indexOf("targetNamespace=") + "targetNamespace=".length() + 1;
                        tns = line.substring(tnsIndex, line.indexOf("\"", tnsIndex));
                        logger.log(Level.INFO, "index: " + tnsIndex + ", tns: " + tns);
                        outputContents.add(line.substring(0, line.length() - 1) + " xmlns:tns=\"" + tns + "\">");
                    } else {
                        outputContents.add(line.replace("type=\"", "type=\"tns:"));
                    }
                }
            }

            // Details
            for (String line : detailsSchemaContents) {
                if (!line.contains("<xs:import") && !line.contains("<?xml") && !line.contains("<xs:schema")) {
                    if (line.contains("type=")) {
                        // References to other types in the same schema
                        if (!line.contains("type=\"xs:")) {
                            outputContents.add(line.replace("type=\"", "type=\"tns:"));
                        } else {
                            outputContents.add(line);
                        }
                    } else {
                        outputContents.add(line);
                        // Type Id
                        if (line.contains("<xs:sequence")) {
                            outputContents.add("<xs:element name=\"id\" type=\"xs:long\" minOccurs=\"0\"/>");
                        }
                    }
                }
            }

            // Schema file
            schemaFile = new File(pathResolver.getRoot(Path.SRC_MAIN_RESOURCES), "/schema.xsd");
            FileUtils.writeLines(schemaFile, outputContents);

            // Root element
            schemaFileContents = FileUtils.readFileToString(schemaFile);
            rootElementIndex = schemaFileContents.indexOf("xs:element name=\"");
            rootElementName = schemaFileContents.substring(rootElementIndex + "xs:element name=\"".length(),
                    schemaFileContents.indexOf("\"", rootElementIndex + "xs:element name=\"".length() + 1));
            schemaFileContents = schemaFileContents.replace("type=\"tns:" + rootElementName + "\"/>", ">");
            schemaFileContents = schemaFileContents.replace("<xs:complexType name=\"" + rootElementName + "\">",
                    "<xs:complexType>");
            schemaFileContents = schemaFileContents.replace("</xs:complexType>", "</xs:complexType></xs:element>");
            FileUtils.writeStringToFile(schemaFile, schemaFileContents);

            FileUtils.forceDelete(schemasDir);
            logger.log(Level.INFO, "XML schemas correctly generated");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while generating entities schemas: " + e.getMessage(), e);
        }
    }

    /**
     * @see mx.itesm.imb.ImbCommands#updateMarshallingConfiguration()
     */
    public void updateMarshallingConfiguration() {
        logger.log(Level.INFO, "Updating Spring configuration...");
    }

    /**
     * 
     * @param controllerName
     * @param controllerPath
     * @throws IOException
     * @throws MethodInvocationException
     * @throws ParseErrorException
     * @throws ResourceNotFoundException
     */
    private void writeImbAspect(final String controllerName, final String packageName, final String controllerPath)
            throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, IOException {
        Writer writer;
        VelocityContext context;
        List<String> packageNames;
        StringBuilder imbTypePackage;

        // Imb Type package name
        imbTypePackage = new StringBuilder("imb.");
        packageNames = Arrays.asList(packageName.replace("web", "domain").split("\\."));
        Collections.reverse(packageNames);
        for (int i = 0; i < packageNames.size(); i++) {
            imbTypePackage.append(packageNames.get(i));
            if (i < (packageNames.size() - 1)) {
                imbTypePackage.append(".");
            }
        }

        // Velocity configuration
        context = new VelocityContext();
        context.put("controllerPackage", packageName);
        context.put("controllerType", controllerName);
        context.put("type", controllerName.replace("Controller", ""));
        context.put("typePackage", packageName.replace("web", "domain"));
        context.put("imbTypePackage", imbTypePackage.toString());

        // TODO: Change to configuration file
        context.put("imbAddress", "http://localhost:8080/tlbus-0.1.0.BUILD-SNAPSHOT");

        writer = new FileWriter(new File(controllerPath, controllerName + "_Roo_Imb.aj"));
        ImbOperationsImpl.aspectTemplate.merge(context, writer);
        writer.close();
    }
}
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.11.22 at 08:56:22 PM CST 
//


package imb.domain.todolist.itesm.mx;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for priority.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="priority">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Low"/>
 *     &lt;enumeration value="Medium"/>
 *     &lt;enumeration value="High"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "priority")
@XmlEnum
public enum Priority {

    @XmlEnumValue("Low")
    LOW("Low"),
    @XmlEnumValue("Medium")
    MEDIUM("Medium"),
    @XmlEnumValue("High")
    HIGH("High");
    private final String value;

    Priority(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Priority fromValue(String v) {
        for (Priority c: Priority.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

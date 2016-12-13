//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.9-03/31/2009 04:14 PM(snajper)-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.06.15 at 12:50:48 PM KST 
//


package com.highbar.tools.mri.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for xStackTrace complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="xStackTrace">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="executionNodes" type="{}xExecutionNode" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="threadId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="totalTime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xStackTrace", propOrder = {
    "executionNodes",
    "threadId",
    "totalTime"
})
public class XStackTrace {

    @XmlElement(nillable = true)
    protected List<XExecutionNode> executionNodes;
    protected String threadId;
    protected long totalTime;

    /**
     * Gets the value of the executionNodes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the executionNodes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExecutionNodes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XExecutionNode }
     * 
     * 
     */
    public List<XExecutionNode> getExecutionNodes() {
        if (executionNodes == null) {
            executionNodes = new ArrayList<XExecutionNode>();
        }
        return this.executionNodes;
    }

    /**
     * Gets the value of the threadId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getThreadId() {
        return threadId;
    }

    /**
     * Sets the value of the threadId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setThreadId(String value) {
        this.threadId = value;
    }

    /**
     * Gets the value of the totalTime property.
     * 
     */
    public long getTotalTime() {
        return totalTime;
    }

    /**
     * Sets the value of the totalTime property.
     * 
     */
    public void setTotalTime(long value) {
        this.totalTime = value;
    }

}
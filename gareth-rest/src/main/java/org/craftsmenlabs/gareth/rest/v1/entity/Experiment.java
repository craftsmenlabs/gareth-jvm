package org.craftsmenlabs.gareth.rest.v1.entity;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

/**
 * Created by hylke on 27/08/15.
 */
@Data
@XmlRootElement
public class Experiment {

    @XmlElement(name = "hash")
    private String hash;
    @XmlElement(name = "experiment_name")
    private String experimentName;
    @XmlElement(name = "baseline_glueline")
    private String baselineGlueLine;
    @XmlElement(name = "assume_glueline")
    private String assumeGlueLine;
    @XmlElement(name = "time_glueline")
    private String timeGlueLine;
    @XmlElement(name = "success_glueline")
    private String successGlueLine;
    @XmlElement(name = "failure_glueline")
    private String failureGlueLine;


}

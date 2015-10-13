package org.craftsmenlabs.gareth.rest.v1.entity;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

/**
 * Created by hylke on 13/10/15.
 */
@Data
@XmlRootElement
public class ExperimentRun {

    @XmlElement(name = "hash")
    private String hash;
    @XmlElement(name = "baseline_execution")
    private LocalDateTime baselineExecution;
    @XmlElement(name = "assume_execution")
    private LocalDateTime assumeExecution;
    @XmlElement(name = "success_execution")
    private LocalDateTime successExecution;
    @XmlElement(name = "failure_execution")
    private LocalDateTime failureExecution;
    @XmlElement(name = "baseline_state")
    private String baselineState;
    @XmlElement(name = "assume_state")
    private String assumeState;
    @XmlElement(name = "success_state")
    private String successState;
    @XmlElement(name = "failure_state")
    private String failureState;
}

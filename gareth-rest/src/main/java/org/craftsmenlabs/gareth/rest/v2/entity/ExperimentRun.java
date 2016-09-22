package org.craftsmenlabs.gareth.rest.v2.entity;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;


@Data
@XmlRootElement
public class ExperimentRun {

    private String hash;
    private LocalDateTime baselineExecution;
    private LocalDateTime assumeExecution;
    private LocalDateTime successExecution;
    private LocalDateTime failureExecution;
    private String baselineState;
    private String assumeState;
    private String successState;
    private String failureState;
}

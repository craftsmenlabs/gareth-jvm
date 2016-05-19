package org.craftsmenlabs.gareth.rest.v1.entity;

import lombok.Data;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

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

    @XmlElement(name = "_links")
    @InjectLinks(value = {
            @InjectLink(value = "experimentruns/{hash}", method = "get", style = InjectLink.Style.ABSOLUTE, rel = "experimentruns", bindings = {
                    @Binding(name = "hash", value = "${instance.hash}")
            }),
            @InjectLink(value = "experiments-rerun/{hash}", method = "get", style = InjectLink.Style.ABSOLUTE, rel = "rerun", bindings = {
                    @Binding(name = "hash", value = "${instance.hash}")
            })
    })
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    private List<Link> links;

}

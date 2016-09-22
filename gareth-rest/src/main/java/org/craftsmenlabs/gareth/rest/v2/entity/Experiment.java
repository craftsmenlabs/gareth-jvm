package org.craftsmenlabs.gareth.rest.v2.entity;

import lombok.Data;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

@Data
@XmlRootElement
public class Experiment {

    private String hash;
    private String experimentName;
    private String baselineGlueLine;
    private String assumeGlueLine;
    private String timeGlueLine;
    private String successGlueLine;
    private String failureGlueLine;

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

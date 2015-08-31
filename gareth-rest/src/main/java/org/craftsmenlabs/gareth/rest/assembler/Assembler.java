package org.craftsmenlabs.gareth.rest.assembler;

/**
 * Created by hylke on 28/08/15.
 */
public interface Assembler<I, O> {

    O assembleOutbound(final I inbound);

    I assembleInbound(final O outbound);
}

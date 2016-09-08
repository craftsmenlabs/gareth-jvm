package org.craftsmenlabs.gareth.rest.assembler;


public interface Assembler<I, O> {

    O assembleOutbound(final I inbound);

    I assembleInbound(final O outbound);
}

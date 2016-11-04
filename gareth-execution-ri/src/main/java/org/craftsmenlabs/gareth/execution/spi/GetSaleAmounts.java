package org.craftsmenlabs.gareth.execution.spi;

import org.craftsmenlabs.gareth.api.annotation.Baseline;
import org.craftsmenlabs.gareth.execution.invoker.DefaultStorage;
import org.springframework.stereotype.Service;

@Service
public class GetSaleAmounts {

    @Baseline(glueLine = "^sale of (.*?)$")
    public void getSaleOfItem(final DefaultStorage storage, final String item) {

    }

}

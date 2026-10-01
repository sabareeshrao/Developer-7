package com.atlasgrid.geoops.project.validation;

import org.springframework.stereotype.Component;

@Component
public class ValidationIntakeSwitch {
    private volatile boolean open = true;

    public boolean isOpen() {
        return open;
    }

    public void pause() {
        open = false;
    }

    public void resume() {
        open = true;
    }
}

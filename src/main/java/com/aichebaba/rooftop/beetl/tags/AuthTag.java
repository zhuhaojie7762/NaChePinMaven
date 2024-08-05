package com.aichebaba.rooftop.beetl.tags;

import org.beetl.core.GeneralVarTagBinding;

public class AuthTag extends GeneralVarTagBinding {
    @Override
    public void render() {
        doBodyRender();
    }
}

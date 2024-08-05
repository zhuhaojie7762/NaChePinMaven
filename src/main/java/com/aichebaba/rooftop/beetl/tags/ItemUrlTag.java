package com.aichebaba.rooftop.beetl.tags;

import com.aichebaba.rooftop.config.AppConfig;
import org.beetl.core.GeneralVarTagBinding;

import java.io.IOException;

public class ItemUrlTag extends GeneralVarTagBinding {

    @Override
    public void render() {
        int id = (int) getAttributeValue("id");
        String url = AppConfig.getItemBaseUrl() + "/" + id;
        try {
            ctx.byteWriter.writeString(url.replaceAll("//", "/"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


package com.choranet.zk;

import org.zkoss.zul.Button;

/**
 *
 * @author RABBAH
 */
public class CategoryButton extends Button {

    private String _include;

    public String getInclude() {
        return _include;
    }

    public void setInclude(String include) {
        this._include = include;
    }

    @Override
    protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
            throws java.io.IOException {
        super.renderProperties(renderer);
        render(renderer, "include", _include);
    }
}

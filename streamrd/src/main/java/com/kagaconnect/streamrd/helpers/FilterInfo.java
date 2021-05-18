package com.kagaconnect.streamrd.helpers;

import com.pedro.encoder.input.gl.render.filters.BaseFilterRender;
import com.pedro.encoder.input.gl.render.filters.BasicDeformationFilterRender;
import com.pedro.encoder.input.gl.render.filters.BlurFilterRender;
import com.pedro.encoder.input.gl.render.filters.CartoonFilterRender;
import com.pedro.encoder.input.gl.render.filters.DuotoneFilterRender;
import com.pedro.encoder.input.gl.render.filters.EarlyBirdFilterRender;
import com.pedro.encoder.input.gl.render.filters.FireFilterRender;
import com.pedro.encoder.input.gl.render.filters.GammaFilterRender;
import com.pedro.encoder.input.gl.render.filters.GreyScaleFilterRender;
import com.pedro.encoder.input.gl.render.filters.HalftoneLinesFilterRender;
import com.pedro.encoder.input.gl.render.filters.Image70sFilterRender;
import com.pedro.encoder.input.gl.render.filters.LamoishFilterRender;
import com.pedro.encoder.input.gl.render.filters.MoneyFilterRender;
import com.pedro.encoder.input.gl.render.filters.NegativeFilterRender;
import com.pedro.encoder.input.gl.render.filters.NoFilterRender;
import com.pedro.encoder.input.gl.render.filters.PixelatedFilterRender;
import com.pedro.encoder.input.gl.render.filters.PolygonizationFilterRender;
import com.pedro.encoder.input.gl.render.filters.RainbowFilterRender;
import com.pedro.encoder.input.gl.render.filters.RippleFilterRender;
import com.pedro.encoder.input.gl.render.filters.SepiaFilterRender;
import com.pedro.encoder.input.gl.render.filters.TemperatureFilterRender;
import com.pedro.encoder.input.gl.render.filters.ZebraFilterRender;

import java.util.ArrayList;
import java.util.List;

public class FilterInfo {
    private boolean bSelected = false;
    private BaseFilterRender filter;
    private boolean bFavourite = false;
    private String text;

    public FilterInfo(String text, BaseFilterRender filter) {
        this.filter = filter;
        this.text = text;
    }

    public BaseFilterRender getFilter() {
        return filter;
    }

    public void setFilter(BaseFilterRender filter) {
        this.filter = filter;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelected(){
        return bSelected;
    }

    public void setSelected(boolean bSelected){
        this.bSelected = bSelected;
    }

    public boolean isFavourite(){
        return bFavourite;
    }

    public void setFavourite(boolean bFavourite){
        this.bFavourite = bFavourite;
    }

    public static List<FilterInfo> getFilters(){
        List<FilterInfo> filters = new ArrayList<>();
        filters.add(new FilterInfo("None", new NoFilterRender()));
        filters.add(new FilterInfo("Deformation", new BasicDeformationFilterRender()));
        filters.add(new FilterInfo("Blur", new BlurFilterRender()));
        filters.add(new FilterInfo("Cartoon", new CartoonFilterRender()));
        filters.add(new FilterInfo("Duotone", new DuotoneFilterRender()));
        filters.add(new FilterInfo("Early Bird", new EarlyBirdFilterRender()));
        filters.add(new FilterInfo("Fire", new FireFilterRender()));
        filters.add(new FilterInfo("Gamma", new GammaFilterRender()));
        filters.add(new FilterInfo("Grey Scale", new GreyScaleFilterRender()));
        filters.add(new FilterInfo("Halftone Lines", new HalftoneLinesFilterRender()));
        filters.add(new FilterInfo("70s Image", new Image70sFilterRender()));
        filters.add(new FilterInfo("Lamoish", new LamoishFilterRender()));
        filters.add(new FilterInfo("Money", new MoneyFilterRender()));
        filters.add(new FilterInfo("Negative", new NegativeFilterRender()));
        filters.add(new FilterInfo("Pixelated", new PixelatedFilterRender()));
        filters.add(new FilterInfo("Polygonization", new PolygonizationFilterRender()));
        filters.add(new FilterInfo("Rainbow", new RainbowFilterRender()));
        filters.add(new FilterInfo("Ripple", new RippleFilterRender()));
        filters.add(new FilterInfo("Sepia", new SepiaFilterRender()));
        filters.add(new FilterInfo("Temperature", new TemperatureFilterRender()));
        filters.add(new FilterInfo("Zebra", new ZebraFilterRender()));
        return filters;
    }
}

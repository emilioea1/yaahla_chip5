package com.chip.parkpro1.interfaces;

import com.chip.parkpro1.data.models.Site;

import java.util.List;

public interface Interactions {
    void onFilterClick(List<String> filters);

    void moveOnSearchedSiteClicked(Site site);
}

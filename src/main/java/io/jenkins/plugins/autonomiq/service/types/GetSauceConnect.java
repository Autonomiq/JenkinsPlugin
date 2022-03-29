package io.jenkins.plugins.autonomiq.service.types;

import java.util.Arrays;

public class GetSauceConnect {
	
	private String[] sauce_connect_ids;

    public void setsauce_connect_ids(String[] sauce_connect_ids) {
    	this.sauce_connect_ids = Arrays.copyOf(sauce_connect_ids,sauce_connect_ids.length);
    }

    public String[] sauce_connect_ids() {
        return Arrays.copyOf(sauce_connect_ids, sauce_connect_ids.length);
    }

}

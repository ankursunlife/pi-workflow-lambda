package com.aarete.pi.claimprocess.beans;

import com.google.gson.Gson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimLineListBean {

	private String status;
	private String level;

	public ClaimLineListBean(String json) {
		Gson gson = new Gson();
		ClaimLineListBean request = gson.fromJson(json, ClaimLineListBean.class);
		this.status = request.getStatus();
		this.level = request.getLevel();
	}

}

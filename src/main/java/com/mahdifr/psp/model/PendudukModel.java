package com.mahdifr.psp.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendudukModel {
	private int id, jenis_kelamin, id_keluarga;
	private String nik, nama, tempat_lahir, agama, pekerjaan, status_perkawinan, status_dalam_keluarga, golongan_darah;
	private Date  tanggal_lahir;
	private boolean is_wni, is_wafat;
	private KeluargaModel keluarga;
	
	public String isIs_wni() {
		if(is_wni)
			return "WNI";
		else
			return "WNA";
	}
	public String isIs_wafat() {
		if(is_wafat)
			return "Wafat";
		else
			return "Hidup";
	}
}
package com.mahdifr.psp.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendudukModel {
	private int id; 
	private String nik;
	
	@NotNull(message = "Tidak Boleh Kosong")
	private int jenis_kelamin, id_keluarga;
	
	@NotNull(message = "Tidak Boleh Kosong")
	@Size(min=1, message = "Tidak Boleh Kosong")
	private String nama, tempat_lahir, agama, pekerjaan, status_perkawinan, status_dalam_keluarga, golongan_darah;
	
	@NotNull(message = "Tidak Boleh Kosong")
	@Pattern(regexp = "\\d{2}(-|\\/)\\d{2}(-|\\/)\\d{4}", message = "Format dd-MM-yyyy")
	private String tanggal_lahir;
	
	@NotNull(message = "Tidak Boleh Kosong")
	private boolean is_wni, is_wafat;
	
	private KeluargaModel keluarga;
	
	public boolean isIs_wni() {
		return is_wni;
	}
	public void setIs_wni(boolean is_wni) {
		this.is_wni = is_wni;
	}
	public boolean isIs_wafat() {
		return is_wafat;
	}
	public void setIs_wafat(boolean is_wafat) {
		this.is_wafat = is_wafat;
	}
}
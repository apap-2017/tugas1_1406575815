package com.mahdifr.psp.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeluargaModel {
	private int id, id_kelurahan;
	private String nomor_kk, alamat, rt, rw;
	private boolean is_tidak_berlaku;
	private List<PendudukModel> penduduk;
	private KelurahanModel kelurahan;
	
	public boolean isIs_tidak_berlaku() {
		return is_tidak_berlaku;
	}
}
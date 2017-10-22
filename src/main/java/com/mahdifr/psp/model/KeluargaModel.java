package com.mahdifr.psp.model;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeluargaModel {
	private Integer id, id_kelurahan;
	private String nomor_kk;
	
	@NotNull(message = "Tidak Boleh Kosong")
	@Size(min=1, max=128, message = "Minimum 1 Karakter, Maximum 256 Karakter")
	private String alamat;
	
	@NotNull(message = "Tidak Boleh Kosong")
	@Size(min=1, max=3, message = "Minimum 1 Karakter, Maximum 3 Karakter")
	private String rt, rw;
	
	@NotNull(message = "Tidak Boleh Kosong")
	private boolean is_tidak_berlaku;
	
	private List<PendudukModel> penduduk;
	private KelurahanModel kelurahan;
	
	public boolean isIs_tidak_berlaku() {
		return is_tidak_berlaku;
	}

	public void setIs_tidak_berlaku(boolean is_tidak_berlaku) {
		this.is_tidak_berlaku = is_tidak_berlaku;
	}
}
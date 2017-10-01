package com.mahdifr.psp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KelurahanModel {
	private int id, id_kecamatan;
	private String kode_kelurahan, nama_kelurahan, kode_pos;
}

package com.mahdifr.psp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KecamatanModel {
	private int id, id_kota;
	private String kode_kecamatan, nama_kecamatan;
}

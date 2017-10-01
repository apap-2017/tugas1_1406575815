package com.mahdifr.psp.model;

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
}

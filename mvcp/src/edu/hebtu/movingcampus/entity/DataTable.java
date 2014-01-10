package edu.hebtu.movingcampus.entity;

import java.util.ArrayList;

public class DataTable<T> {
	private ArrayList<T> dbTable;

	public ArrayList<T> getDbTable() {
		return this.dbTable;
	}

	public void setDbTable(ArrayList<T> paramArrayList) {
		this.dbTable = paramArrayList;
	}

	public void setDbTableArray(Object[] arrayOfObject) {
		ArrayList localArrayList = new ArrayList();
		for (int i = 0;; i++) {
			if (i >= arrayOfObject.length) {
				this.dbTable = localArrayList;
				return;
			}
			localArrayList.add(arrayOfObject[i]);
		}
	}
}

/*
 * Location: /tmp/apksrc_tmp_9gTLc/classes-dex2jar.jar Qualified Name:
 * com.caii101.bean.DataTable JD-Core Version: 0.6.2
 */
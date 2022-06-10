/*
 * CPQKeys: An evaluation of various graph canonization algorithms.
 * Copyright (C) 2022  Roan Hofland (roan@roanh.dev).  All rights reserved.
 * GitHub Repository: https://github.com/RoanH/CPQKeys
 *
 * CPQKeys is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CPQKeys is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dev.roanh.cpqkeys;

public class VertexData<T>{
	private int id;
	private T data;
	
	public VertexData(int id, T data){
		this.id = id;
		this.data = data;
	}
	
	public int getID(){
		return id;
	}
	
	public T getData(){
		return data;
	}
	
	@Override
	public boolean equals(Object obj){
		return data.equals(obj);
	}
	
	@Override
	public int hashCode(){
		return data.hashCode();
	}
}

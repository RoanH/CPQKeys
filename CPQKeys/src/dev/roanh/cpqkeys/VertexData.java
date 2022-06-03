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

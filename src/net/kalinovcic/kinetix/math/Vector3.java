package net.kalinovcic.kinetix.math;

public class Vector3
{
	public double x;
    public double y;
    public double z;
	
	public Vector3(double x, double y, double z)
	{
		set(x, y, z);
	}
	
	public Vector3(Vector3 v)
	{
		set(v);
	}
	
	public Vector3 clone()
	{
		return new Vector3(this);
	}
	
	public Vector3 set(double x, double y, double z)
	{
		this.x = x;
        this.y = y;
        this.z = z;
		return this;
	}
	
	public Vector3 set(Vector3 v)
	{
		x = v.x;
		y = v.y;
		z = v.z;
		return this;
	}
	
	public Vector3 add(double v)
	{
		x += v;
		y += v;
		z += v;
		return this;
	}
	
	public Vector3 add(Vector3 v)
	{
		x += v.x;
		y += v.y;
        z += v.z;
		return this;
	}
	
	public Vector3 sub(double v)
	{
		x -= v;
		y -= v;
        z -= v;
		return this;
	}
	
	public Vector3 sub(Vector3 v)
	{
		x -= v.x;
		y -= v.y;
        z -= v.z;
		return this;
	}
	
	public Vector3 mul(double v)
	{
		x *= v;
		y *= v;
        z *= v;
		return this;
	}
	
	public Vector3 mul(Vector3 v)
	{
		x *= v.x;
		y *= v.y;
        z *= v.z;
		return this;
	}
	
	public Vector3 div(double v)
	{
		x /= v;
        y /= v;
        z /= v;
		return this;
	}
	
	public Vector3 div(Vector3 v)
	{
		x /= v.x;
        y /= v.y;
        z /= v.z;
		return this;
	}
    
    public double dot(Vector3 v)
    {
        return x*v.x + y*v.y + z*v.z;
    }
    
    public Vector3 cross(Vector3 v)
    {
        return new Vector3(y*v.z - z*v.y,
                           z*v.x - x*v.z,
                           x*v.y - y*v.x);
    }
	
	public double length()
	{
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public Vector3 normalize()
	{
		div(length());
		return this;
	}
	
	public Vector3 normal()
	{
		return clone().normalize();
	}
	
	@Override
	public String toString()
	{
	    return String.format("(%.2f, %.2f, %.2f)", x, y, z);
	}
}

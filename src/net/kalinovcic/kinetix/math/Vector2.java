package net.kalinovcic.kinetix.math;

public class Vector2
{
	public double x;
	public double y;
	
	public Vector2(double x, double y)
	{
		set(x, y);
	}
	
	public Vector2(Vector2 v)
	{
		set(v);
	}
	
	public Vector2 clone()
	{
		return new Vector2(this);
	}
	
	public Vector2 set(double x, double y)
	{
		this.x = x;
		this.y = y;
		return this;
	}
	
	public Vector2 set(Vector2 v)
	{
		x = v.x;
		y = v.y;
		return this;
	}
	
	public Vector2 add(double v)
	{
		x += v;
		y += v;
		return this;
	}
	
	public Vector2 add(Vector2 v)
	{
		x += v.x;
		y += v.y;
		return this;
	}
	
	public Vector2 sub(double v)
	{
		x -= v;
		y -= v;
		return this;
	}
	
	public Vector2 sub(Vector2 v)
	{
		x -= v.x;
		y -= v.y;
		return this;
	}
	
	public Vector2 mul(double v)
	{
		x *= v;
		y *= v;
		return this;
	}
	
	public Vector2 mul(Vector2 v)
	{
		x *= v.x;
		y *= v.y;
		return this;
	}
	
	public double dot(Vector2 v)
	{
		return x*v.x + y*v.y;
	}
	
	public Vector2 div(double v)
	{
		x /= v;
		y /= v;
		return this;
	}
	
	public Vector2 div(Vector2 v)
	{
		x /= v.x;
		y /= v.y;
		return this;
	}
	
	public double length()
	{
		return Math.sqrt(x * x + y * y);
	}
	
	public Vector2 normalize()
	{
		div(length());
		return this;
	}
	
	public Vector2 normal()
	{
		return clone().normalize();
	}
	
	@Override
	public String toString()
	{
	    return String.format("(%.2f, %.2f)", x, y);
	}
}

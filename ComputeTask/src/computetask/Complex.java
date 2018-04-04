package computetask;

/**
 * Class that implements complex numbers and some arithmetic operations.
 * This is not a full-featured implementation; only the operations needed
 * for the Mandelbrot demo have been provided.
 * 
 * @author E. Stark
 * @version 20180330
 */
public class Complex {
    
    private final double realPart;
    private final double imagPart;
    
    public Complex(double real, double imag) {
        realPart = real;
        imagPart = imag;
    }
    
    public double getRealPart() {
        return realPart;
    }
    
    public double getImagPart() {
        return imagPart;
    }
    
    public Complex add(Complex other) {
        return new Complex(realPart + other.realPart, imagPart + other.imagPart);
    }
    
    public Complex mul(Complex other) {
        return new Complex(realPart * other.realPart - imagPart * other.imagPart,
                           realPart * other.imagPart + imagPart * other.realPart);
    }
    
    public double modulus() {
        return(Math.sqrt(realPart * realPart + imagPart * imagPart));
    }
    
    @Override
    public boolean equals(Object other) {
        if(other == null)
            return false;
        if(other == this)
            return true;
        if(getClass() != other.getClass())
            return false;
        Complex o = (Complex)other;
        return(realPart == o.realPart && imagPart == o.imagPart);
    }
    
    @Override
    public String toString() {
        return getRealPart() + "+" + getImagPart() + "i";
    }
    
}

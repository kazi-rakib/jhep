/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.physics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jlab.jnp.pdg.PDGDatabase;
import org.jlab.jnp.pdg.PDGParticle;

/**
 *
 * @author Gagik Gavalian
 * @date   05/16/2015
 */

public class Particle implements Comparable<Particle>{

    /**
     * Particle Lorentz Vector, momentum and time component (initialized with electron with zero momentum)
     */
    private LorentzVector particleVector = new LorentzVector(0.0,0.0,0.0,0.0005);
    /**
     * particle vertex (the origin coordinates), initialized at coordinate system origin.
     */
    private Vector3 particleVertex = new Vector3(0.0,0.0,0.0);
    /**
     * Particle LUND ID, found in class PDGDatabase. initialized with electron PID
     */
    private int particleLund_ID = 11;
    /**
     * Particle ID used in GEANT. 3 = electron in GEANT.
     */
    private int particleGeant_ID = 3;
    /**
     * Particle charge (can be set by user, usually taken from PDG database when initialized by Lund ID).
     */
    private int particleCharge = -1;
    
    /**
     * Parent particle PID.
     */
    private int parentParticle = 0;
    /**
     * Particle status indicates if the particle is a final product or a particle that decayed.
     * status=21 is for initial particles in the process.
     * status=11 is for secondary particles in the reaction that later decayed.
     * status=1  is for final particles.
     * status=13 is for quarks that were produced in the reaction.
     */
    private int particleStatus = 1;
    
    HashMap<String, Double> particleProperties;

    
    public Particle() {
        this.initParticleWithMass(0.0, 0., 0., 0., 0., 0., 0.);
    }

    public Particle(Particle p) {
        this.initParticle(p.pid(), p.px(), p.py(), p.pz(), p.vertex().x(), p.vertex().y(), p.vertex().z());
    }
    
    public Particle(int pid, double px, double py, double pz, double vx, double vy, double vz) {
        this.initParticle(pid, px, py, pz, vx, vy, vz);
    }
    
    public Particle(int pid, double px, double py, double pz) {
        this.initParticle(pid, px, py, pz, 0.0, 0.0, 0.0);
    }
    
    public Particle(int pid, double mass, byte charge, double px, double py, double pz, double vx, double vy, double vz) {
        this.initParticleWithMass(mass, px, py, pz, vx, vy, vz);
        particleLund_ID = pid;
        particleCharge = (byte) charge;
    }
    
    public static Particle createWithMassCharge(double mass, int charge ,double px, double py, double pz, double vx, double vy, double vz){
        Particle p = new Particle();
        p.initParticleWithMass(mass, px, py, pz, vx, vy, vz);
        p.particleCharge = (byte) charge;
        return p;
    }
    
    public static Particle createWithPid(int pid ,double px, double py, double pz, double vx, double vy, double vz){
        Particle p = new Particle(pid,px,py,pz,vx,vy,vz);
        return p;
    }
    
    public final void initParticleWithMass(double mass, double px, double py, double pz, double vx, double vy, double vz) {
        particleCharge = 0;
        particleVector = new LorentzVector();
        particleVertex = new Vector3(vx, vy, vz);
        particleVector.setPxPyPzM(px, py, pz, mass);
        particleProperties = new HashMap<String, Double>();
    }
    
    public final void initParticle(int pid, double px, double py, double pz, double vx, double vy, double vz) {
        PDGParticle particle = PDGDatabase.getParticleById(pid);
        if (particle == null) {
            System.out.println("Particle: warning. particle with pid=" + pid + " does not exist.");
            initParticleWithMass(0., px, py, pz, vx, vy, vz);
            particleLund_ID = 0;
            particleGeant_ID = 0;
        } else {
            initParticleWithMass(particle.mass(), px, py, pz, vx, vy, vz);
            particleLund_ID = pid;
            particleGeant_ID = 0;// particle.gid();
            particleCharge = (byte) particle.charge();
        }
    }
    
    /**
     * Change the particle momenta from it's original value to new value
     * 
     * @param mom
     *            new particle momenta
     */
    public void setP(double mom) {
        double mag = this.vector().p();
        double factor = mom / mag;
        this.vector().setPxPyPzM(this.vector().vect().x() * factor, this.vector().vect().y() * factor, this.vector().vect().z() * factor,
                this.mass());
    }
    
    public void setTheta(double theta) {
        this.vector().vect().setMagThetaPhi(this.vector().p(), theta, this.vector().phi());
    }
    
    public void changePid(int pid) {
        PDGParticle part = PDGDatabase.getParticleById(pid);
        if (part == null) {
            System.err.println("[Particle::changePid]  error ---> unknown particle id " + pid);
            return;
        }
        particleVector.setPxPyPzM(this.particleVector.px(), this.particleVector.py(), this.particleVector.pz(), part.mass());
        
        particleLund_ID = pid;
    }
    
    public void setParticleWithMass(double mass, byte charge, double px, double py, double pz, double vx, double vy, double vz) {
        particleVector.setPxPyPzM(px, py, pz, mass);
        particleLund_ID = 0;
        particleGeant_ID = 0;
        particleCharge = charge;
    }
    
    public void setVector(int pid, double px, double py, double pz, double vx, double vy, double vz) {
        PDGParticle particle = PDGDatabase.getParticleById(pid);
        if (particle == null) {
            System.out.println("Particle: warning. particle with pid=" + pid + " does not exist.");
            particleLund_ID = 0;
        } else {
            particleVector.setPxPyPzM(px, py, pz, particle.mass());
            particleVertex.setXYZ(vx, vy, vz);
            particleLund_ID = pid;
            particleGeant_ID = particle.gid();
            particleCharge = (byte) particle.charge();
        }
    }
    /**
     * set status word for the particle. 
     * @param status status word
     */
    public void setStatus(int status){
        this.particleStatus = status;
    }
    /**
     * get status word for the particle.
     * @return integer status word.
     */
    public int  getStatus(){
        return this.particleStatus;
    }
    
    public double px() {
        return this.vector().px();
    }
    
    public double py() {
        return this.vector().py();
    }
    
    public double pz() {
        return this.vector().pz();
    }
    
    public double p() {
        return this.vector().p();
    }
    
    public double theta() {
        return this.vector().theta();
    }
    
    public double phi() {
        return this.vector().phi();
    }
    
    public double e() {
        return this.vector().e();
    }
    
    public double vx() {
        return this.particleVertex.x();
    }
    
    public double vy() {
        return this.particleVertex.y();
    }
    
    public double vz() {
        return this.particleVertex.z();
    }
    
    public void clearProperties() {
        particleProperties.clear();
    }
    
    public void setVector(int pid, Vector3 nvect, Vector3 nvert) {
        PDGParticle particle = PDGDatabase.getParticleById(pid);
        if (particle == null) {
            System.out.println("Particle: warning. particle with pid=" + pid + " does not exist.");
            particleLund_ID = 0;
        } else {
            particleVector.setVectM(nvect, particle.mass());
            particleVertex.setXYZ(nvert.x(), nvert.y(), nvert.z());
            particleLund_ID = pid;
            particleGeant_ID = particle.gid();
            particleCharge = (byte) particle.charge();
        }
    }
    
    public double euclideanDistance(Particle part) {
        double xx = (this.vector().px() - part.vector().px());
        double yy = (this.vector().py() - part.vector().py());
        double zz = (this.vector().pz() - part.vector().pz());
        return Math.sqrt(xx * xx + yy * yy + zz * zz);
    }
    
    public double cosTheta(Particle part) {
        if (part.vector().p() == 0 || this.vector().p() == 0)
            return -1;
        return part.vector().vect().dot(particleVector.vect()) / (part.vector().vect().mag() * particleVector.vect().mag());
    }
    
    void initParticleWithMassSquare(double mass2, double px, double py, double pz, double vx, double vy, double vz) {
        particleCharge = 0;
        particleVector = new LorentzVector();
        particleVertex = new Vector3(vx, vy, vz);
        particleVector.setPxPyPzE(px, py, pz, Math.sqrt(px * px + py * py + pz * pz + mass2));
        particleProperties = new HashMap<String, Double>();
    }
    
    public void initParticleWithPidMassSquare(int pid, int charge, double mass2, double px, double py, double pz, double vx, double vy,
            double vz) {
        particleLund_ID = pid;
        particleCharge = (byte) charge;
        particleVector = new LorentzVector();
        particleVertex = new Vector3(vx, vy, vz);
        particleVector.setPxPyPzE(px, py, pz, Math.sqrt(px * px + py * py + pz * pz + mass2));
        particleProperties = new HashMap<String, Double>();
    }
    
    public void setVector(LorentzVector nvec, Vector3 nvert) {
        particleVector = nvec;
        particleVertex = nvert;
    }
    /**
     * returns mass of the particle. short cat to Particle.vector().mass()
     * @return mass of the particle
     */
    public double mass() {
        return particleVector.mass();
    }
    /**
     * returns squared mass of the particle. shortcut to Particle.vector().mass2()
     * @return mass squared
     */
    public double mass2() {
        return particleVector.mass2();
    }
    /**
     * returns charge of the particle.
     * @return charge
     */
    public int charge() {
        return (int) particleCharge;
    }
    
    public void charge(int charge){
        this.particleCharge = charge;
    }
    /**
     * returns Lund ID for the particle.
     * @return Lund id
     */
    public int pid() {
        return particleLund_ID;
    }
    /**
     * set PID for particle. This method only sets the PID without changing the 
     * mass for particle. You can use changePid(pid) to readjust the lorentz vector.
     * @param pid LUND particle id
     */
    public void pid(int pid){
        this.particleLund_ID = pid;
    }
    /**
     * returns GEANT ID of the particle.
     * @return GEANT id
     */
    public int gid() {
        return particleGeant_ID;
    }
    /**
     * returns reference to particle lorentz vector.
     * @return lorentz vector
     */
    public final LorentzVector vector() {
        return particleVector;
    }
    /**
     * returns the vertex of the particle.
     * @return vector object
     */
    public final Vector3 vertex() {
        return particleVertex;
    }
    /**
     * returns the value of the property with given name. If property does not
     * exist, 0.0 will be returned. No warning message is printed.
     * @param pname property name
     * @return property value
     */
    public double getProperty(String pname) {
        if (particleProperties.containsKey(pname) == true)
            return particleProperties.get(pname);
        return 0.0;
    }
    /**
     * returns id for parent particle. usually if the particle is the final product this 
     * number is 1
     * @return parent particle is
     */
    public int getParentParticle(){
        return this.parentParticle;
    }
    
    public void setParentParticle(int parent){
        this.parentParticle = parent;
    }
    /**
     * transforms particle into the frame of parent particle given as
     * and argument.
     * @param parent particle frame
     * @return same particle in a parent frame
     */
    public Particle inFrame(Particle parent) {
        Vector3 boost = parent.vector().boostVector();
        Vector3 boostm = new Vector3(-boost.x(), -boost.y(), -boost.z());
        particleVector.boost(boostm);
        return this;
    }
    /**
     * returns property of a particle by string, used by string parser.
     * @param pname
     * @return 
     */
    public double get(String pname) {
        if (pname.compareTo("mass") == 0)
            return particleVector.mass();
        if (pname.compareTo("mass2") == 0)
            return particleVector.mass2();
        if (pname.compareTo("theta") == 0)
            return particleVector.theta();
        if (pname.compareTo("phi") == 0)
            return particleVector.phi();
        if (pname.compareTo("p") == 0)
            return particleVector.p();
        if (pname.compareTo("mom") == 0)
            return particleVector.p();
        if (pname.compareTo("e") == 0)
            return particleVector.e();
        if (pname.compareTo("px") == 0)
            return particleVector.px();
        if (pname.compareTo("py") == 0)
            return particleVector.py();
        if (pname.compareTo("pz") == 0)
            return particleVector.pz();
        if (pname.compareTo("vx") == 0)
            return particleVertex.x();
        if (pname.compareTo("vy") == 0)
            return particleVertex.y();
        if (pname.compareTo("vz") == 0)
            return particleVertex.z();
        if (pname.compareTo("vertx") == 0)
            return particleVertex.x();
        if (pname.compareTo("verty") == 0)
            return particleVertex.y();
        if (pname.compareTo("vertz") == 0)
            return particleVertex.z();
        
        System.out.println("[Particle::get] ERROR ----> variable " + pname + "  is not defined");
        return 0.0;
    }

    /**
     * Checks if the particle has property with given name.
     * @param pname property name
     * @return ture is exists, false otherwise
     */    
    public boolean hasProperty(String pname) {
       return particleProperties.containsKey(pname);
    }
    /**
     * adds property to the property list of the particle
     * @param pname property name
     * @param value property value
     */
    public void setProperty(String pname, double value) {
        // if(particleProperties.containsKey(pname)==true)
        particleProperties.put(pname, value);
    }
    /**
     * returns string containing string representation of key,value pairs
     * of properties.
     * @return string printout of properties map
     */
    public String propertyString() {
        StringBuilder str = new StringBuilder();
        Iterator it = particleProperties.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            str.append(String.format("%12s : %f", pairs.getKey(), pairs.getValue()));
        }
        return str.toString();
    }
    /**
     * returns a string representing the particle in LUND format. Used in The PhysicsEvent
     * class to output into LUND file.
     * @return LUND string of the particle
     */
    public String toLundString() {
        StringBuilder str = new StringBuilder();
        str.append(String.format("%3.0f. %4d %6d %2d %2d %9.4f %9.4f %9.4f ", (float) particleCharge, this.particleStatus, 
                particleLund_ID, this.parentParticle , (int) 0,
                particleVector.px(), particleVector.py(), particleVector.pz()));
        
        str.append(String.format("%9.4f %9.4f %11.4f %9.4f %9.4f", particleVector.e(), particleVector.mass(), particleVertex.x(), particleVertex.y(),
                particleVertex.z()));
        return str.toString();
    }
    /**
     * returns string representation of the particle. Unlike LUND string, this string contains
     * particle momentum, and angles in degrees, it is more readable.
     * @return string representation for particle
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(String.format("%6d %3d %9.5f %9.5f %9.5f %9.5f %9.5f %9.5f %9.5f", particleLund_ID, particleCharge, particleVector.mass(),
                particleVector.p(), Math.toDegrees(particleVector.theta()), Math.toDegrees(particleVector.phi()), particleVertex.x(), particleVertex.y(),
                particleVertex.z()));
        return str.toString();
    }
    /**
     * Sets the content of this particle to the particle passed as an argument.
     * @param part particle to copy from.
     */
    public void copyParticle(Particle part) {
        this.particleVector.setPxPyPzM(part.vector().px(), part.vector().py(), part.vector().pz(), part.vector().mass());
        this.particleVertex.setXYZ(part.vertex().x(), part.vertex().y(), part.vertex().z());        
        particleLund_ID  = part.pid();
        particleGeant_ID = part.gid();
        particleCharge   = part.charge();
        particleStatus = part.getStatus();
        parentParticle = part.getParentParticle();
        particleCharge = part.charge();
    }
    
    public void copy(Particle part) {
        this.copyParticle(part);
    }
    
    public void combine(Particle cpart, int sign) {
        particleLund_ID = 0;
        if (sign >= 0) {
            particleVector.add(cpart.vector());
        } else {
            particleVector.sub(cpart.vector());
        }
        
        particleCharge += cpart.charge();
        
        // if(this.mass()==0.0&&this.vector().p()==0.0)
        // {
        // System.err.println(" pid = " + cpart.pid());
        this.particleVertex.setXYZ(cpart.vertex().x(), cpart.vertex().y(), cpart.vertex().z());
        // } else {
        /*
        * Line3D pl = new Line3D(); Line3D pn = new Line3D();
        *
        * pl.setOrigin(partVertex.x(), partVertex.y(), partVertex.z() );
        *
        * pl.setEnd(partVertex.x()+partVector.vect().x(), partVertex.y()+partVector.vect().y(), partVertex.z()+partVector.vect().z());
        *
        * pn.setOrigin( cpart.vertex().x(), cpart.vertex().y(), cpart.vertex().z());
        *
        * pn.setEnd( cpart.vertex().x() + cpart.vector().px(), cpart.vertex().y() + cpart.vector().py(), cpart.vertex().z() + cpart.vector().pz() ); Line3D doca =
        * pl.distance(pn); Point3D docam = doca.middle(); this.partVertex.setXYZ(docam.x(), docam.y(), docam.z());
        *
        * /* partVertex.setXYZ(cpart.vertex().x(), cpart.vertex().y(),cpart.vertex().z());
        */
    }
    
    public Vector3 particleDoca(Particle cpart) {
        /*
        * Line3D pl = new Line3D(); Line3D pn = new Line3D();
        *
        * pl.setOrigin(partVertex.x(), partVertex.y(), partVertex.z() );
        *
        * pl.setEnd(partVertex.x()+partVector.vect().x(), partVertex.y()+partVector.vect().y(), partVertex.z()+partVector.vect().z());
        *
        * pn.setOrigin( cpart.vertex().x(), cpart.vertex().y(), cpart.vertex().z());
        *
        * pn.setEnd( cpart.vertex().x() + cpart.vector().px(), cpart.vertex().y() + cpart.vector().py(), cpart.vertex().z() + cpart.vector().pz() );
        *
        * Line3D doca = pl.distance(pn); Point3D docam = doca.middle(); //this.partVertex.setXYZ(docam.x(), docam.y(), docam.z()); return new
        * BasicVector(docam.x(),docam.y(),docam.z());
        */
        return new Vector3();
    }
    /**
     * Compares two particles complying with Comparable interface.
     * The priority is given to charged particles over neutral.
     * In case of two particles have same momentum priority is given
     * to one with higher momentum.
     * a negative int - if this lt that
     * 0              - if this == that
     * a positive int - if this gt that
     * @param o object that this class is being compared to
     * @return -1,0,1 depending how the object are compared
     */
    @Override
    public int compareTo(Particle o) {
        /**
         * Always make sure that electron is set in the first position
         */
        if(this.pid()==11&&o.pid()!=11) return -1;
        if(o.pid()==11&&this.pid()!=11) return  1;
        /**
         * For particles with same PID, sorting will happen on the basis
         * of their momentum.
         */
        if(this.pid()==o.pid()){
            if(Math.abs(o.p()-this.p())<0.00001) return 0;
            return (o.p()>this.p())?-1:1;
        }
        /**
         * For the rest of the particles, sorting is done according to
         * their GEANT PID. Easier because GEANT ID's are positive numbers.
         */
        if(o.gid()==this.gid()) return 0;
        return o.gid()>this.gid()?-1:1;
    }
    
}

package com.example.calldetect.messageriePrincipale.Sim;

public class Sim_class {
    private int imageSim;
    private String nomSim;

    public Sim_class(int imageSim, String nomSim) {
        this.imageSim = imageSim;
        this.nomSim = nomSim;
    }

    public int getImageSim() {
        return imageSim;
    }

    public void setImageSim(int imageSim) {
        this.imageSim = imageSim;
    }

    public String getNomSim() {
        return nomSim;
    }

    public void setNomSim(String nomSim) {
        this.nomSim = nomSim;
    }

    @Override
    public String toString() {
        return "Sim_class{" +
                "imageSim='" + imageSim + '\'' +
                ", nomSim='" + nomSim + '\'' +
                '}';
    }
}

package nes.mappers;

public class INESMapperList {
    private Mapper[] mappers;
    
    public INESMapperList() {
        mappers = new Mapper[256];
        
        mappers[0] = new NROM();
        mappers[1] = new MMC1();
    }
    
    public Mapper getMapper(int mapper) {
        return mappers[mapper];
    }
}

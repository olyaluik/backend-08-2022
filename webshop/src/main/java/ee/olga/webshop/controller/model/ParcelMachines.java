package ee.olga.webshop.controller.model;

import lombok.Data;

import java.util.List;

@Data
public class ParcelMachines {
    private List<OmnivaParcelMachine> omniva;
    private List<SmartPostParcelMachine> smartpost;
}

package org.sixdouglas.formation.spring.irrigation.producer;

import org.sixdouglas.formation.spring.irrigation.Drop;
import org.sixdouglas.formation.spring.irrigation.Dropper;
import org.sixdouglas.formation.spring.irrigation.GreenHouse;
import org.sixdouglas.formation.spring.irrigation.Row;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public final class GreenHouseProducer {
    private static final List<GreenHouse> greenHouses = List.of(GreenHouse.builder()
                .id(1)
                .name("In-House plants")
                .row(Row.builder()
                        .id(1)
                        .name("A")
                        .dropper(Dropper.builder().id(1).name("I-A-1").build())
                        .dropper(Dropper.builder().id(2).name("I-A-2").build())
                        .dropper(Dropper.builder().id(3).name("I-A-3").build())
                        .dropper(Dropper.builder().id(4).name("I-A-4").build())
                        .build())
                .row(Row.builder()
                        .id(2)
                        .name("B")
                        .dropper(Dropper.builder().id(1).name("I-B-1").build())
                        .dropper(Dropper.builder().id(2).name("I-B-2").build())
                        .dropper(Dropper.builder().id(3).name("I-B-3").build())
                        .build())
                .build(),
                GreenHouse.builder()
                .id(2)
                .name("Bamboos")
                .row(Row.builder()
                        .id(1)
                        .name("A")
                        .dropper(Dropper.builder().id(1).name("B-A-1").build())
                        .dropper(Dropper.builder().id(2).name("B-A-2").build())
                        .dropper(Dropper.builder().id(3).name("B-A-3").build())
                        .dropper(Dropper.builder().id(4).name("B-A-4").build())
                        .dropper(Dropper.builder().id(5).name("B-A-5").build())
                        .dropper(Dropper.builder().id(6).name("B-A-6").build())
                        .dropper(Dropper.builder().id(7).name("B-A-7").build())
                        .build())
                .build(),
                GreenHouse.builder()
                .id(3)
                .name("Fruit trees")
                .row(Row.builder()
                        .id(1)
                        .name("A")
                        .dropper(Dropper.builder().id(1).name("F-A-1").build())
                        .dropper(Dropper.builder().id(4).name("F-A-4").build())
                        .build())
                .row(Row.builder()
                        .id(2)
                        .name("B")
                        .dropper(Dropper.builder().id(1).name("F-B-1").build())
                        .dropper(Dropper.builder().id(2).name("F-B-2").build())
                        .dropper(Dropper.builder().id(3).name("F-B-3").build())
                        .build())
                .row(Row.builder()
                        .id(3)
                        .name("C")
                        .dropper(Dropper.builder().id(1).name("F-C-1").build())
                        .dropper(Dropper.builder().id(2).name("F-C-2").build())
                        .dropper(Dropper.builder().id(2).name("F-C-2").build())
                        .dropper(Dropper.builder().id(3).name("F-C-3").build())
                        .build())
                .build());


    public static Flux<Drop> getDrops() {
        Flux<Drop> dropsFlux = Flux.empty();

        //TODO go through all Greenhouses
        //TODO    go through all Row
        //TODO       go through all Dropper
        //TODO          Create a flux that will emit a Drop every 10 millis seconds using the buildDrop() function
        //TODO          then merge this new flux int the dropsFlux

        for (GreenHouse greenHouse: greenHouses) {
            for (Row row: greenHouse.getRows()) {
                for (Dropper dropper: row.getDroppers()) {
                    Flux<Drop> newFlux = Flux.interval(Duration.ofMillis(10)).flatMap(aLong -> buildDrop(greenHouse, row, dropper));
                    dropsFlux = Flux.merge(newFlux,dropsFlux);
                }
            }
        }

        return dropsFlux;
    }

    private static Mono<Drop> buildDrop(GreenHouse greenHouse, Row row, Dropper dropper) {
        //TODO make this function return a Mono.error if the Dropper is Broken

        return Mono.just(Drop.builder()
                .greenHouseId(greenHouse.getId())
                .rowId(row.getId())
                .dropperId(dropper.getId())
                .instant(Instant.now())
                .build());
    }


    public static Mono<GreenHouse> getDropper(int greenHouseId, int rowId, int dropperId) {

        final Optional<GreenHouse> greenHouseOptional = greenHouses.stream()
                .filter(greenHouse1 -> greenHouse1.getId() == greenHouseId)
                .findFirst();

        if (greenHouseOptional.isEmpty()) {
            return Mono.empty();
        }

        GreenHouse greenHouse = greenHouseOptional.get();
        final Optional<Row> rowOptional = greenHouse.getRows().stream().filter(row -> row.getId() == rowId).findFirst();
        if (rowOptional.isEmpty()) {
            return Mono.empty();
        }

        Row row = rowOptional.get();
        final Optional<Dropper> dropperOptional = row.getDroppers().stream().filter(dropper -> dropper.getId() == dropperId).findFirst();
        if(dropperOptional.isEmpty()){
            return Mono.empty();
        }

        return getJust(greenHouse, row, dropperOptional.get());
    }

    private static Mono<GreenHouse> getJust(GreenHouse house, Row row, Dropper dropper) {
        //TODO Build a new Greenhouse that will contain a newly built Row that will contain a newly built Dropper
        //TODO    using the data of the given objects
        return Mono.just(GreenHouse
                .builder()
                .id(house.getId())
                .name(house.getName())
                .row(Row
                        .builder()
                        .id(row.getId())
                        .name(row.getName())
                        .dropper(Dropper
                                .builder()
                                .id(dropper.getId())
                                .name(dropper.getName())
                                .broken(dropper.isBroken())
                                .build())
                        .build())
                .build());
    }
}

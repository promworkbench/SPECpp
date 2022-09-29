package org.processmining.specpp.postprocessing;

import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.specpp.config.parameters.OutputPathParameters;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.util.FileUtils;
import org.processmining.specpp.util.PathTools;

import java.io.FileWriter;
import java.io.IOException;

public class PlaceExporter extends AbstractGlobalComponentSystemUser implements CollectionOfPlacesPostProcessor {

    public PlaceExporter(OutputPathParameters outputPathParameters) {
        this.outputPathParameters = outputPathParameters;
    }


    public static class Builder extends ComponentSystemAwareBuilder<PostProcessor<CollectionOfPlaces, CollectionOfPlaces>> {

        private final DelegatingDataSource<OutputPathParameters> outputPathParameters = new DelegatingDataSource<>();

        public Builder() {
            globalComponentSystem().require(ParameterRequirements.OUTPUT_PATH_PARAMETERS, outputPathParameters);
        }

        @Override
        protected PostProcessor<CollectionOfPlaces, CollectionOfPlaces> buildIfFullySatisfied() {
            return new PlaceExporter(outputPathParameters.getData());
        }
    }

    private final OutputPathParameters outputPathParameters;


    @Override
    public CollectionOfPlaces postProcess(CollectionOfPlaces result) {
        String filePath = outputPathParameters.getFilePath(PathTools.OutputFileType.MISC_EXPORT, "places", ".txt");

        try (FileWriter fileWriter = FileUtils.createOutputFileWriter(filePath)) {
            fileWriter.write("" + result.getPlaces().size());
            fileWriter.write("\n");
            for (Place place : result.getPlaces()) {
                fileWriter.write(place.toString());
                fileWriter.write("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

}

package com.palsplate.system.akka.gpx;

import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.InputFactoryImpl;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AkkaGpxTest {

    public ArrayList<LatLon> extractLatLonFromFile() throws XMLStreamException, IOException {

        final AsyncXMLInputFactory f = new InputFactoryImpl();
        AsyncXMLStreamReader<AsyncByteArrayFeeder> sc = null;

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("abfahrtCar.gpx").getFile());

        //init array with file length
        byte[] xmlBytes = new byte[(int) file.length()];

        FileInputStream fis = new FileInputStream(file);
        fis.read(xmlBytes); //read file into bytes[]
        fis.close();

        sc = f.createAsyncForByteArray();

        ArrayList<LatLon> latlonList = new ArrayList<LatLon>();

        int bufferFeedLength = 1 ;
        int currentByteOffset = 0 ;
        int type ;
        do{
            while( (type = sc.next()) == AsyncXMLStreamReader.EVENT_INCOMPLETE ) {
                byte[] buffer = new byte[]{ xmlBytes[ currentByteOffset ] } ;
                currentByteOffset ++ ;
                sc.getInputFeeder().feedInput( buffer, 0, bufferFeedLength ) ;
                if( currentByteOffset >= xmlBytes.length ) {
                    sc.getInputFeeder().endOfInput() ;
                }
            }
            switch( type ) {

                case XMLEvent.START_DOCUMENT :
//                    System.out.println( "start document" ) ;
                    break ;

                case XMLEvent.START_ELEMENT :
//                    System.out.println( "start element: " + sc.getName());

                    if(sc.getName().toString().contains("trkpt")){

                        latlonList.add(new LatLon(Double.parseDouble(sc.getAttributeValue(0)),
                                Double.parseDouble(sc.getAttributeValue(1))));
//                        System.out.println(sc.getAttributeName(0) + " " + sc.getAttributeValue(0));
//                        System.out.println(sc.getAttributeName(1) + " " + sc.getAttributeValue(1));
                    }
                    break ;

                case XMLEvent.CHARACTERS :
//                    System.out.println( "characters: " + sc.getText()) ;
                    break ;

                case XMLEvent.END_ELEMENT :
//                    System.out.println( "end element: " + sc.getName()) ;
                    break ;

                case XMLEvent.END_DOCUMENT :
//                    System.out.println( "end document" ) ;
                    break ;

                default :
                    break ;
            }
        } while( type != XMLEvent.END_DOCUMENT ) ;
        sc.close();

        return latlonList;

    }

    public static void main(String[] args) throws IOException, XMLStreamException {

        AkkaGpxTest jt = new AkkaGpxTest();
        ArrayList<LatLon> output = jt.extractLatLonFromFile();

        System.out.println("output: " + output.toString());
        System.out.println("output size: " + output.size());

    }
}
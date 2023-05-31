package byow.Test;

import org.junit.Test;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import byow.Core.Engine;

public class TileTest {
    @Test
    public void test(){
        Engine engine1 = new Engine();
        Engine engine2 = new Engine();
        String [] args1 = {"-s","n5643591630821615871s"};
        String [] args2 = {"-s","n5643591630821615871s"};
        //engine1.interactWithInputString("n5643591630821615871s");
        //engine1.ter.renderFrame(engine1.world.getTiles());
        //engine2.ter.renderFrame(engine2.world.getTiles());

        //assertThat(engine1.interactWithInputString("n5643591630821615871swwaawd")).isEqualTo(engine1.interactWithInputString("n5643591630821615871swwaawd"));
        assertThat(engine1.interactWithInputString("n5643591630821615871s")).isEqualTo(engine1.interactWithInputString("n5643591630821615871s"));

    }
    @Test
    public void test1(){
        Engine engine1 = new Engine();
        Engine engine2 = new Engine();
        engine2.interactWithInputString("n1392967723524655428sddsaawws:q");
        assertThat(engine1.interactWithInputString("n1392967723524655428sddsaawwsaddw")).isEqualTo(engine2.interactWithInputString("laddw"));
    }
}

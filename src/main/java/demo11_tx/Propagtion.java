package demo11_tx;

/**
 * 事务传播行为
 */
public class Propagtion {

    public void a(){
        //begin
        //步骤
         b();
        //commit
    }

    public void b(){
    }
}

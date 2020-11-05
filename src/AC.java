import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @Classname AC
 * @Description
 * @Date 2020/11/5 18:05
 * @Created by shuaif
 */
public class AC {

    private State originState; //根节点 0状态
    private List<State> allStates = new ArrayList<>();


    public AC(String[] pattern) {
        originState = new State(0);
        allStates.add(originState);

        constructAutomata(pattern);
    }


    /**
     * 构建自动机
     * @param pattern 模式串集合
     */
    private void constructAutomata(String[] pattern){
        for (String s : pattern) {
            enter(s);
        }
        originState.setFailure(originState);
        failure();
    }

    /**
     * 添加新的状态节点到自动机上面
     * @param s 模式串
     */
    private void enter(String s) {
        byte[] key = s.getBytes();
        int i = 0;
        State state = originState;
        while (true) {
            assert state != null;
            if (!state.getTrans().containsKey(key[i])) break; //当前状态没有对应的转移状态
            state = _goto(state,key[i]);
            i++;
        }
        for (int p = i; p < key.length; p++){
            System.out.println(key[p] + "  "+ state.getState());
            State newState = new State(allStates.size());
            state.addGoto(key[p], newState);
            state = newState;
            allStates.add(newState);
        }
        state.addOutput(new String(key));
    }


    /**
     * goto 函数：状态转移
     * @param state
     * @param input
     * @return
     */
    private State _goto(State state, byte input) {
        if (state.getTrans().containsKey(input)) {
            return state.getTrans().get(input);
        }
        if (state.getState() == 0) {
            return originState;
        }
        return null;
    }

    /**
     * failure 失效函数，补全输出集
     * @return
     */
    private void failure() {
        Queue<State> queue = new LinkedList<>();
        for (State child : originState.getChildren()) { //深度为1的节点failure值为0
            queue.offer(child);
            child.setFailure(originState);
        }
        while (!queue.isEmpty()) {
            State r = queue.poll();
            for (Byte key : r.getTrans().keySet()) {
                State s = r.getTrans().get(key);
                if (!queue.contains(s)){
                    queue.offer(s);
                }
                State state = r.getFailure();
                while (_goto(state,key) == null) {
                    state = state.getFailure();
                }
                s.setFailure(_goto(state,key));
                for (String output : s.getFailure().getOutput()) {
                    s.addOutput(output);
                }
            }
        }
    }

    public void run(String input){
        byte[] target = input.getBytes();
        State state = originState;
        for (byte b : target) {
            while (_goto(state,b) == null) {
                state = state.getFailure();
            }
            state = _goto(state,b);
            assert state != null;
            if (state.isTerminalState()) {
                System.out.println("State : " + state.getState());
                System.out.print("Output : { ");
                for (String s : state.getOutput()) {
                    System.out.print(s + ",");
                }
                System.out.println("}");
            }
        }
    }

    public void showAutomata() {
        for (State state : allStates) {
            System.out.println("ID :"+state.getState());
            System.out.print("Trans : ");
            for (Byte key : state.getTrans().keySet()) {
                System.out.print("[ " + new String(new byte[] {key}) + "," + state.getTrans().get(key).getState() +  " ]");
            }
            System.out.println();
            System.out.println("Failure : " + state.getFailure().getState());
            if (state.isTerminalState()) {
                System.out.print("Output : [");
                for (String s : state.getOutput()) {
                    System.out.print(s + " ");
                }
                System.out.println("]");
            }
        }
    }

    public static void main(String[] args) {
        String[] pattern = {"she","he","his"};
        DoubleArrayAC daac = new DoubleArrayAC(pattern);
        daac.showAutomata();
        daac.run("he his shehe");
    }
}

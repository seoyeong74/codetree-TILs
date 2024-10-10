import java.util.*;
public class Main {
    static int N;
    //쉬고 있는 채점기 queue
    static PriorityQueue<Integer> waiting_judger = new PriorityQueue<>();

    //채점 대기 큐 정보
    static class Waiting_Info implements Comparable<Waiting_Info>{
        int priority, time;
        String url, domain;
        Waiting_Info(int priority, int time, String url){
            this.priority = priority;
            this.time = time;
            this.url = url;

            String[] split_s = url.split("/");
            this.domain = split_s[0];
        }

        @Override
        public int compareTo(Waiting_Info o2){
            //우선순위가 작을 수록, 순열
            if (this.priority != o2.priority){
                return Integer.compare(this.priority, o2.priority);
            }
            //시간이 더 작을수록, 순열
            return Integer.compare(this.time, o2.time);
        }
    }
    static PriorityQueue<Waiting_Info> waiting_q = new PriorityQueue<>();
    //채점 대기 큐에 있는 url을 저장하는 map, key: url
    static HashMap<String, Boolean> waiting_map = new HashMap<>();

    //채점 정보
    static class Juging_Info{
        int start;
        String domain;
        Juging_Info(int start, String domain){
            this.start = start;
            this.domain = domain;
        }
    }
    //채점 중인 정보를 저장하는 map, key: j_id
    static HashMap<Integer, Juging_Info> juging_map = new HashMap<>();
    
    //각 domain의 최신 정보를 저장하는 map
    static class Domain_Info{
        int start, end;
        boolean juging = true;

        public int get_gap(){
            return end - start;
        }

        Domain_Info(int start){
            this.start = start;
            this.end = end;
        }
    }
    //key: 도메인
    static HashMap<String, Domain_Info> domain_map = new HashMap<>();


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int Q = sc.nextInt();

        for(int q = 0; q < Q; q++){
            int command = sc.nextInt();

            //채점기 준비
            if (command == 100){
                N = sc.nextInt();
                //쉬는 채점기 준비
                for(int i = 1; i <= N; i++){
                    waiting_judger.offer(i);
                }

                //u0 채점 요청
                String u0 = sc.next();
                add_waiting(u0, 1, 0);
            }
            //채점 요청
            else if(command == 200){
                int t = sc.nextInt();
                int p = sc.nextInt();
                String url = sc.next();

                add_waiting(url, p, t);
            }
            //채점 시도
            else if(command == 300){
                int t = sc.nextInt();

                try_judging(t);
            }
            //채점 종료
            else if(command == 400){
                int t = sc.nextInt();
                int j_id = sc.nextInt();

                stop_judging(t, j_id);
            }
            //대기 큐 조회
            else if(command == 500){
                int t = sc.nextInt();
                System.out.println(waiting_q.size());
            }
            // if (q > 14){
            //     System.out.println("waiting judger");
            //     for(int i: waiting_judger){
            //         System.out.print(i + " ");
            //     }
            //     System.out.println();

            //     System.out.println("waiting queue");
            //     for(Waiting_Info i: waiting_q){
            //         System.out.println(i.time + " " + i.priority + " " + i.url);
            //     }
            //     for(String i: waiting_map.keySet()){
            //         System.out.println(i);
            //     }

            //     System.out.println("judging");
            //     for(int i: juging_map.keySet()){
            //         System.out.println(juging_map.get(i).start + " " + juging_map.get(i).domain);
            //     }

            //     // System.out.println("history");
            //     // for(Juging_Info i: juging_map){
            //     //     System.out.println(i.start + " " + i.domain);
            //     // }

            //     System.out.println("");
            // }
        }
    }

    public static void stop_judging(int t, int j_id){
        //채점 중인 id인지 확인
        if(!juging_map.containsKey(j_id)) return;

        //도메인 업데이트
        Juging_Info info = juging_map.get(j_id);
        String domain = info.domain;

        Domain_Info domain_info = domain_map.get(domain);
        //종료시간 업데이트
        domain_info.end = t;
        //채점 완료
        domain_info.juging = false;

        //채점 중인 맵에서 삭제
        juging_map.remove(j_id);

        //채점기 다시 쉬기
        waiting_judger.offer(j_id);
    }

    public static void try_judging(int time){
        //남는 채점기가 있는지 확인한다.
        if (waiting_judger.isEmpty()) return;

        //채점이 불가능한 것들을 저장하는 곳
        ArrayList<Waiting_Info> put_again = new ArrayList<>();

        while(!waiting_q.isEmpty()){
            Waiting_Info info = waiting_q.poll();

            //채점이 가능 불가능하다면 다음으로 넘어간다.
            if (!possible_judging(info, time)){
                put_again.add(info);
                continue;
            }

            //채점을 시작한다.
            int judger = waiting_judger.poll();
            //채점 대기 맵에서 url 제거
            waiting_map.remove(info.url);
            //채점 정보 map 저장
            juging_map.put(judger, new Juging_Info(time, info.domain));
            //도메인 저장
            domain_map.put(info.domain, new Domain_Info(time));
            break;
        }

        //채점이 불가능했던 것들을 다시 넣는다.
        for(Waiting_Info w: put_again){
            waiting_q.offer(w);
        }
    }

    static boolean possible_judging(Waiting_Info info, int t){
        //한번도 채점이 시도된 적 없는 도메인
        if(!domain_map.containsKey(info.domain)) return true;

        Domain_Info domain_info = domain_map.get(info.domain);
        //채점이 진행 중인지?
        if (domain_info.juging) return false;
        //부적절한 채점인지?
        if ((domain_info.start + 3 * domain_info.get_gap()) > t) return false;

        return true;
    }

    public static void add_waiting(String url, int p, int t){
        String[] split_url = url.split("/");
        String domain = split_url[0];

        //채점 대기큐에 task와 일치하는 url 있다면 넘어간다.
        if (waiting_map.containsKey(url)) return;

        //채점 대기큐에 넣는다.
        waiting_q.offer(new Waiting_Info(p, t, url));
        //채점 대기중인 url을 map에 저장한다.
        waiting_map.put(url, true);
    }
}
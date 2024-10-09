import java.util.*;

public class Main {
    static int N, M, K;
    static int[][] power_map;
    static int[][] turn_map;

    static int tower_num = 0;

    static boolean[][] demaged;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        N = sc.nextInt();
        M = sc.nextInt();
        K = sc.nextInt();

        power_map = new int[N][M];
        turn_map = new int[N][M];
        demaged = new boolean[N][M];

        for(int i = 0; i < N; i++){
            for(int j = 0; j < M; j++){
                power_map[i][j] = sc.nextInt();

                if(power_map[i][j] != 0) tower_num++;
            }
        }

        int turn = 1;

        // for(Point p: road){
        //     System.out.println(p.x + " " + p.y);
        // }

        // for(int i = 0; i < result.length; i++){
        //     System.out.println(result[i].x + " " + result[i].y);
        // }

        while(turn <= K){
            demaged = new boolean[N][M];

            Info[] result = sorting();

            ArrayList<Point> road = layzer(result[0], result[1]);
            int demage = result[0].power + N + M;
            power_map[result[0].x][result[0].y] = demage;
            turn_map[result[0].x][result[0].y] = turn;

            // System.out.println(result[0].x + " " + result[0].y);
            // System.out.println(result[1].x + " " + result[1].y);

            //레이저 공격을 한 것
            if(road.size() != 0){
                for(Point p: road){
                    // System.out.println(p.x + " " + p.y);
                    demaged[p.x][p.y] = true;

                    if (p.x == result[1].x && p.y == result[1].y){
                        //0보다 밑으로 피가 줄어즐 수 없음
                        power_map[p.x][p.y] = Math.max(
                            0,
                            power_map[p.x][p.y] - (demage)
                        );
                    }
                    //목표물이 아니면 /2 만큼의 피해
                    else {
                        power_map[p.x][p.y] = Math.max(
                            0,
                            power_map[p.x][p.y] - (demage / 2)
                        );
                    }

                    if (power_map[p.x][p.y] == 0) tower_num--;
                }
            }
            //레이저 공격을 못함
            //포탄 공격을 함
            else{
                anthor(result[0], result[1], demage);
            }

            // System.out.println("after attack");
            // for(int i = 0; i < N; i++){
            //     for(int j = 0; j < M; j++){
            //         System.out.print(power_map[i][j] + " ");
            //     }
            //     System.out.println();
            // }
            // System.out.println();

            add(result[0]);

            // System.out.println("after add");
            // for(int i = 0; i < N; i++){
            //     for(int j = 0; j < M; j++){
            //         System.out.print(power_map[i][j] + " ");
            //     }
            //     System.out.println();
            // }
            // System.out.println();
            if(tower_num <= 1) break;
            turn++;
        }

        int max = 0;
        for(int i = 0; i < N; i++){
            for(int j = 0; j < M; j++){
                // System.out.print(power_map[i][j] + " ");
                max = Math.max(max, power_map[i][j]);
            }
            // System.out.println();
        }

        System.out.println(max);
    }

    static void add(Info hit){
        for(int i = 0; i < N; i++){
            for(int j = 0; j < M; j++){
                if (i == hit.x && j == hit.y) continue;
                if (demaged[i][j]) continue;
                if (power_map[i][j] == 0) continue;

                power_map[i][j]++;
            }
        }
    }

    static int[] a_dx = {0, -1, -1, -1,  0, 1, 1, 1};
    static int[] a_dy = {-1, -1, 0, 1,  1, 1, 0, -1};

    static void anthor(Info hit, Info hitted, int demage){

        // System.out.println("test");
        for(int d = 0; d < 8; d++){
            int ax = set_idx(hitted.x + a_dx[d], N);
            int ay = set_idx(hitted.y + a_dy[d], M);

            // System.out.println(ax + " " + ay);

            if (power_map[ax][ay] == 0) continue;
            if (hit.x == ax && hit.y == ay) continue;

            demaged[ax][ay] = true;
            // System.out.println(ax + " " + ay + " " + (power_map[ax][ay] - demage /2));
            power_map[ax][ay] = Math.max(
                0, 
                power_map[ax][ay] - demage /2
            );

            if (power_map[ax][ay] == 0) tower_num--;
        }

        power_map[hitted.x][hitted.y] = Math.max(
            0, 
            power_map[hitted.x][hitted.y] - demage
        );
        demaged[hitted.x][hitted.y] = true;

        if (power_map[hitted.x][hitted.y] == 0) tower_num--;
    }

    static class Point{
        int x, y;
        Point(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    static class RoadInfo{
        int x, y;
        ArrayList<Point> road = new ArrayList<>();
        RoadInfo(int x, int y){
            this.x = x;
            this.y = y;
        }
        RoadInfo(int x, int y, ArrayList<Point> road){
            this.x = x;
            this.y = y;
            this.road = road;
        }
    }

    //우하좌상
    static int[] dx = {0, 1, 0, -1};
    static int[] dy = {1, 0, -1, 0};

    static ArrayList<Point> layzer(Info hit, Info hitted){
        Deque<RoadInfo> q = new ArrayDeque<>();
        boolean[][] visited = new boolean[N][M];

        q.addLast(new RoadInfo(hit.x, hit.y));
        visited[hit.x][hit.y] = true;

        while(!q.isEmpty()){
            RoadInfo cur = q.poll();

            for(int d = 0; d < 4; d++){
                int nx = set_idx(cur.x + dx[d], N);
                int ny = set_idx(cur.y + dy[d], M);

                //부서진 포탑은 못감
                if (power_map[nx][ny] == 0) continue;
                if (visited[nx][ny]) continue;
                //목표물에 도착
                if (nx == hitted.x && ny == hitted.y){
                    cur.road.add(new Point(nx, ny));
                    return cur.road;
                }

                //목표물에 도착하지 않았으면 다시 큐에
                ArrayList<Point> new_road = new ArrayList<>();
                for(Point p: cur.road){
                    new_road.add(p);
                }
                new_road.add(new Point(nx, ny));
                visited[nx][ny] = true;

                q.addLast(new RoadInfo(nx, ny, new_road));
            }
        }

        return new ArrayList<>();
    }

    static int set_idx(int idx, int max){
        if (idx < 0) return idx + max;
        else if (idx >= max) return idx - max;
        else return idx;
    }

    static class Info{
        int x, y;
        int power, turn;
        Info(int x, int y, int power, int turn){
            this.x = x;
            this.y = y;
            this.power = power;
            this.turn = turn;
        }
    }

    static Info[] sorting(){
        ArrayList<Info> info_list = new ArrayList<>();

        for(int i = 0; i < N; i++){
            for(int j = 0; j < M; j++){
                //부서진 포탑 제거
                if (power_map[i][j] == 0) continue;

                info_list.add(new Info(i, j, power_map[i][j], turn_map[i][j]));
            }
        }

        info_list.sort(new Comparator<Info>(){
            @Override
            public int compare(Info o1, Info o2){
                //공격력 순으로 비교
                if (o1.power != o2.power){
                    return Integer.compare(o1.power, o2.power);
                }

                //공격한 순서, 역순
                if (o1.turn != o2.turn){
                    return Integer.compare(o2.turn, o1.turn);
                }

                //행과 열의 합, 이건 역순
                if ((o1.x + o1.y) != (o2.x + o2.y)){
                    return Integer.compare((o2.x + o2.y), (o1.x + o1.y));
                }

                //열 값으로 비교, 이것도 역순
                return Integer.compare(o2.y, o1.y);
            }
        });

        Info[] result = new Info[2];

        // for(Info test: info_list){
        //     System.out.println(test.x + " " + test.y + " " + test.turn);
        // }

        result[0] = info_list.get(0);
        result[1] = info_list.get(info_list.size() - 1);

        return result;
    }
}
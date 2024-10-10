import java.util.*;

public class Main {
    static int N, M, K;
    //1-9 벽
    //-1 출구
    static int[][] map;

    static class Human{
        int x, y;
        Human(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
    static ArrayList<Human> hlist = new ArrayList<>();
    static int gx = 0, gy = 0;

    static int move_sum = 0;
    static int min_length = Integer.MAX_VALUE;
    static int ulx = 0, uly = 0;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        N = sc.nextInt();
        M = sc.nextInt();
        K = sc.nextInt();

        map = new int[N + 1][N + 1];

        for(int i = 1; i <= N; i++){
            for(int j = 1; j <= N; j++){
                map[i][j] = sc.nextInt();
            }
        }

        for(int i = 0; i < M; i++){
            int x = sc.nextInt();
            int y = sc.nextInt();
            hlist.add(new Human(x, y));
        }

        gx = sc.nextInt();
        gy = sc.nextInt();

        int time = 1;
        while(time <= K){
            move();
            rotation();

            if (hlist.isEmpty()) break;
            time++;
        }

        System.out.println(move_sum);
        System.out.println(gx + " " + gy);

        // move();
        // System.out.println(ulx + " " + uly + " " + min_length);
        // rotation();
        // for(int i = 0; i < hlist.size(); i++){
        //     Human h = hlist.get(i);
        //     System.out.println(h.x + " " + h.y);
        // }
        // System.out.println();
        // for(int i = 1; i <= N; i++){
        //     for(int j = 1; j <= N; j++){
        //         System.out.print(map[i][j] + " ");
        //     }
        //     System.out.println();
        // }
    }

    public static void rotation(){
        int[][] new_map = new int[N + 1][N + 1];
        boolean[][] visited = new boolean[N + 1][N + 1];
        boolean change = false;
        
        //정사각형부터 채우기
        for(int i = 0; i <= min_length; i++){
            for(int j = 0; j <= min_length; j++){
                int pre_i = ulx + i, pre_j = uly + j;
                int new_i = ulx + j, new_j = uly + min_length - i;

                visited[new_i][new_j] = true;
                new_map[new_i][new_j] = Math.max(map[pre_i][pre_j] - 1, 0);

                if (!change && gx == pre_i && gy == pre_j){
                    gx = new_i;
                    gy = new_j;
                    change = true;
                }
                // System.out.println(pre_i + " " + pre_j + " " + new_i + " " + new_j);
            }
        }

        //사람 로테이션
        for(int i = 0; i < hlist.size(); i++){
            Human h = hlist.get(i);
            //사각형 안이 아님
            if (!visited[h.x][h.y]) continue;

            int nx = ulx + (h.y - uly);
            int ny = uly + min_length - (h.x - ulx);
            h.x = nx;
            h.y = ny;
        }

        //나머지 채우기
        for(int i = 1; i <= N; i++){
            for(int j = 1; j <= N; j++){
                if(visited[i][j]) continue;
                new_map[i][j] = map[i][j];
            }
        }

        map = new_map;
    }

    //상하좌우
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};

    public static void move(){
        min_length = Integer.MAX_VALUE;
        ulx = 0;
        uly = 0;

        for(int i = 0; i < hlist.size(); i++){
            Human h = hlist.get(i);
            int cur_dis = Math.abs(gx - h.x) + Math.abs(gy - h.y);
            int nx = h.x;
            int ny = h.y;

            boolean move = false;

            for(int d = 0; d < 4; d++){
                nx = h.x + dx[d];
                ny = h.y + dy[d];

                int dis = Math.abs(gx - nx) + Math.abs(gy - ny);

                //맵 밖으로 나가는 것
                if(nx < 1 || ny < 1 || nx > N || ny > N) continue;
                //최단 거리가 가까워지지 않으면
                if (cur_dis <= dis) continue;
                //벽이 있다면
                if (map[nx][ny] > 0) continue;

                move = true;
                break;
            }

            //움직이지 않음
            if (!move) {
                get_min(h.x, h.y);
                continue;
            };

            //움직임
            h.x = nx;
            h.y = ny;
            move_sum++;

            //출구로 도착했는지 확인
            if (h.x == gx && h.y == gy){
                hlist.remove(i);
                i--;
                continue;
            }

            get_min(h.x, h.y);
        }
    }

    public static void get_min(int x, int y){
        //정사각형 길이 구하기
        int length = Math.max(
            Math.abs(x - gx),
            Math.abs(y - gy)
        );

        //더 작거나 같은 정사각형이 아니면
        if (min_length < length) return;

        //정사각형 좌표 구하기
        int c_ulx = 
            Math.max(
                1,
                Math.max(x, gx) - length
            );
        int c_uly = 
            Math.max(
                1,
                Math.max(y, gy) - length
            );

        //같은 크기일 때
        if(length == min_length){
            //r 좌표 비교
            if (ulx > c_ulx){
                ulx = c_ulx;
                uly = c_uly;
                return;
            }
            //c 좌표 비교
            else if (ulx == c_ulx && uly > c_uly){
                ulx = c_ulx;
                uly = c_uly;
                return;
            }

            //아니라면 업데이트 안함
            return;
        }

        //더 작은 크기라면
        ulx = c_ulx;
        uly = c_uly;
        min_length = length;
        return;
    }
}
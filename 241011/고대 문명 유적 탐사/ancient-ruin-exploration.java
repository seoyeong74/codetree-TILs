import java.util.*;
public class Main {
    static int K, M;
    static int[][] map = new int[5][5];
    static Deque<Integer> num_q = new ArrayDeque<>();
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        K = sc.nextInt();
        M = sc.nextInt();
        
        int test = 1;
        for(int i = 0; i < 5; i++){
            for(int j = 0; j< 5; j++){
                map[i][j] = sc.nextInt();
            }
        }

        // int[][] test_map = rotate_90(1, 1);
        // for(int i = 0; i < 5; i++){
        //     for(int j = 0; j< 5; j++){
        //         System.out.print(test_map[i][j] + " ");
        //     }
        //     System.out.println();
        // }
        // System.out.println();

        // Result test_result = get_num(test_map);
        // for(int i = 0; i < 5; i++){
        //     for(int j = 0; j< 5; j++){
        //         System.out.print(test_map[i][j] + " ");
        //     }
        //     System.out.println();
        // }
        // System.out.println();


        for(int i = 0; i < M; i++){
            int m = sc.nextInt();
            num_q.add(m);
        }

        for(int k = 0; k < K; k++){
            int[][] max_new_map = new int[5][5];
            int max_num = 0;
            int max_rotate = 360;
            ArrayList<Point> removed = new ArrayList<>();

            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    int[][] r90_map = rotate_90(i,j);
                    int[][] r180_map = rotate_180(i,j);
                    int[][] r270_map = rotate_270(i,j);

                    Result r90 = get_num(r90_map);
                    Result r180 = get_num(r180_map);
                    Result r270 = get_num(r270_map);

                    if (max_num < r90.num || (max_num == r90.num && max_rotate > 90)){
                        max_new_map = r90.new_map;
                        max_num = r90.num;
                        removed = r90.removed;
                        max_rotate = 90;
                    }
                    if (max_num < r180.num || (max_num == r90.num && max_rotate > 180)){
                        max_new_map = r180.new_map;
                        max_num = r180.num;
                        removed = r180.removed;
                        max_rotate = 180;
                    }
                    if (max_num < r270.num || (max_num == r90.num && max_rotate > 270)){
                        max_new_map = r270.new_map;
                        max_num = r270.num;
                        removed = r270.removed;
                        max_rotate = 270;
                    } 
                }
            }

            //찾아낸 유물이 없다면 그만한다.
            if (max_num == 0) break;

            //벽면을 다시 채우고 유물 연쇄를 시작한다.
            map = max_new_map;
            int ans = max_num;
            while(true){
                // for(int i = 0; i < 5; i++){
                //     for(int j = 0; j< 5; j++){
                //         System.out.print(map[i][j] + " ");
                //     }
                //     System.out.println();
                // }
                // System.out.println();
                removed.sort(new Comparator<Point>(){
                    @Override
                    public int compare(Point o1, Point o2){
                        if(o1.y != o2.y){
                            return Integer.compare(o1.y, o2.y);
                        }
                        return Integer.compare(o2.x, o1.x);
                    }
                });

                for(Point p: removed){
                    int to_fill_num = num_q.poll();
                    map[p.x][p.y] = to_fill_num;
                }

                // for(int i = 0; i < 5; i++){
                //     for(int j = 0; j< 5; j++){
                //         System.out.print(map[i][j] + " ");
                //     }
                //     System.out.println();
                // }
                // System.out.println();

                //유물 연쇄
                Result r = get_num(map);

                //유물이 없어지지 않으면 나감
                if(r.num == 0) break;

                ans += r.num;
                map = r.new_map;
                removed = r.removed;
            }

            System.out.print(ans + " ");
        }
    }

    static class Result{
        int[][] new_map;
        ArrayList<Point> removed;
        int num;
        Result(int[][] new_map, int num, ArrayList<Point> list){
            this.new_map = new_map;
            this.num = num;
            this.removed = list;
        }
    }

    static class Point{
        int x, y;
        Point(int x, int y){
            this.x = x;
            this.y = y;
        }
    };

    //상하좌우
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, 1, -1};

    public static Result get_num(int[][] find_map){
        boolean visited[][] = new boolean[5][5];
        ArrayList<Point> to_remove = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                if(visited[i][j]) continue;
                if(find_map[i][j] == 0) continue;

                ArrayList<Point> candidate = new ArrayList<>();
                Deque<Point> q = new ArrayDeque<>();
                Point init = new Point(i, j);
                candidate.add(init);
                q.add(init);
                visited[i][j] = true;

                int same_num = find_map[i][j];

                while(!q.isEmpty()){
                    Point cur = q.poll();
                    // System.out.println(cur.x + " " + cur.y)
                    for(int d = 0; d < 4; d++){
                        int nx = cur.x + dx[d];
                        int ny = cur.y + dy[d];

                        //맵을 벗어날일 때
                        if(nx < 0 || ny < 0 || nx >= 5 || ny >= 5) continue;
                        //이미 방문한 곳일 때
                        if(visited[nx][ny]) continue;
                        //같은 수가 아닐 때
                        if(find_map[nx][ny] != same_num) continue;

                        Point p = new Point(nx, ny);
                        visited[nx][ny] = true;
                        candidate.add(p);
                        q.add(p);
                    }
                }

                //3개 이상 연결되어 있지 않을 때
                if (candidate.size() < 3) continue;

                //제거할 포인트에 넣어준다.
                to_remove.addAll(candidate);
            }
        }

        //제거되는 유물들을 제거한다.
        for(Point p: to_remove){
            find_map[p.x][p.y] = 0;
        }

        return new Result(find_map, to_remove.size(), to_remove);
    }

    public static int[][] rotate_90(int sx, int sy){
        boolean[][] visited = new boolean[5][5];
        int[][] new_map = new int[5][5];
        //90도 회전
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                int new_x = sx + j;
                int new_y = sy + (2-i);
                visited[sx + i][sy + j] = true;
                new_map[new_x][new_y] = map[sx+i][sy+j];
            }
        }

        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                if (visited[i][j]) continue;
                new_map[i][j] = map[i][j];
            }
        }

        return new_map;
    }

    public static int[][] rotate_180(int sx, int sy){
        boolean[][] visited = new boolean[5][5];
        int[][] new_map = new int[5][5];
        //90도 회전
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                int new_x = sx + (2-i);
                int new_y = sy + (2-j);
                visited[sx + i][sy + j] = true;
                new_map[new_x][new_y] = map[sx+i][sy+j];
            }
        }

        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                if (visited[i][j]) continue;
                new_map[i][j] = map[i][j];
            }
        }

        return new_map;
    }

    public static int[][] rotate_270(int sx, int sy){
        boolean[][] visited = new boolean[5][5];
        int[][] new_map = new int[5][5];
        //90도 회전
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                int new_x = sy + (2-j);
                int new_y = sx + i;
                visited[sx + i][sy + j] = true;
                new_map[new_x][new_y] = map[sx+i][sy+j];
            }
        }

        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                if (visited[i][j]) continue;
                new_map[i][j] = map[i][j];
            }
        }

        return new_map;
    }
}
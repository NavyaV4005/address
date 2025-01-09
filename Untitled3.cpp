#include <stdio.h>
#include <stdlib.h>
int main() {
    int tracks[] = {55, 58, 60, 70, 18, 90, 150, 160, 184};
    int n = sizeof(tracks) / sizeof(tracks[0]);
    int initial_position = 50;  
    int total_head_movement = 0;
    int current_position = initial_position;
    for (int i = 0; i < n; i++) {
        total_head_movement += abs(current_position - tracks[i]);
        current_position = tracks[i];
    }
    float average_head_movement = (float)total_head_movement / n;
    printf("Track positions: ");
    for (int i = 0; i < n; i++) {
        printf("%d ", tracks[i]);
    }
    printf("\n");
    printf("Initial head position: %d\n", initial_position);
    printf("Total head movement: %d\n", total_head_movement);
    printf("Average head movement: %.2f\n", average_head_movement);
    return 0;
}

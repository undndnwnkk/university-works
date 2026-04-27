#include <iostream>
#include <utility>
#include <vector>
#include <map>

using namespace std;

int main() {
    int n;
    cin >> n;
    vector<int> flowers(n);
    map<int, pair<int, int>> repeat;

    for (int i = 0; i < n; i++) {
        cin >> flowers[i];
    }

    int l = 0; int r = 1; repeat[flowers[l]] = {1, 0}; repeat[flowers[r]] = {0, 1};
    
    while (r < n) {
        if (repeat[flowers[r]].first == 0) repeat[flowers[r]].second = r;

        if (repeat[flowers[l]].first < 3 && repeat[flowers[r]].first < 3) {
            
        }

        r++;

    }
    return 0;
}
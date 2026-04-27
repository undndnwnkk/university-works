#include <iostream>
#include <vector>

using namespace std;

int main() {
  int n, m;
  cin >> n >> m;

  vector<int> memory(n, -1);
  vector<int> start(m + 1, -1);

  for (int i = 1; i <= m; i++) {
    long long x;
    cin >> x;

    if (x > 0) {
      int K = (int)x;
      int pos = -1;
      int l = 0;

      while (l < n) {
        if (memory[l] != -1) {
          l++;
          continue;
        }

        if (l > 0 && memory[l - 1] == -1) {
          l++;
          continue;
        }

        int r = l;
        int len = 0;

        while (r < n && memory[r] == -1 && len < K) {
          r++;
          len++;
        }

        if (len == K) {
          pos = l;
          break;
        } else {
          l = r + 1;
        }
      }

      if (pos == -1) {
        cout << -1 << "\n";
        start[i] = -1;
      } else {
        for (int j = pos; j < pos + K; j++) {
          memory[j] = i;
        }
        start[i] = pos;
        cout << (pos + 1) << "\n";
      }

    } else {
      int T = (int)(-x);
      if (T < 1 || T > m) {
        continue;
      }

      if (start[T] == -1) {
        continue;
      }

      int pos = start[T];
      while (pos < n && memory[pos] == T) {
        memory[pos] = -1;
        pos++;
      }
    }
  }

  return 0;
}
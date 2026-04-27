#include <iostream>
#include <set>
#include <unordered_set>
#include <utility>
#include <vector>

const int INFINITY = 1000000000;

using namespace std;

int main() {
  int n, k, p;
  cin >> n >> k >> p;

  vector<int> nums(p);

  for (int i = 0; i < p; i++) {
    cin >> nums[i];
  }

  vector<int> last(n + 1, -1);
  vector<int> next(p, INFINITY);

  for (int i = p - 1; i >= 0; i--) {
    int currentElement = nums[i];
    if (last[currentElement] == -1) {
      next[i] = INFINITY;
    } else {
      next[i] = last[currentElement];
    }

    last[currentElement] = i;
  }

  unordered_set<int> onFloor;
  set<pair<int, int>> heap;
  vector<int> currentNext(n + 1, INFINITY);

  int res = 0;

  for (int i = 0; i < p; i++) {
    int x = nums[i];
    int nextPosition = next[i];

    if (onFloor.count(x)) {
      heap.erase({currentNext[x], x});
      heap.insert({nextPosition, x});
      currentNext[x] = nextPosition;
      continue;
    }

    res++;

    if ((int)onFloor.size() == k) {
      auto it = prev(heap.end());
      int victim_id = it->second;
      heap.erase(it);
      onFloor.erase(victim_id);
      currentNext[victim_id] = INFINITY;
    }

    onFloor.insert(x);
    heap.insert({nextPosition, x});
    currentNext[x] = nextPosition;
  }

  cout << res << "\n";

  return 0;
}
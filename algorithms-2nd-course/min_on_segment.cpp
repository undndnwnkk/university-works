#include <deque>
#include <iostream>
#include <vector>
using namespace std;

int main() {
  int n, k;
  cin >> n >> k;
  vector<int> nums(n);
  for (int i = 0; i < n; i++) {
    cin >> nums[i];
  }

  deque<int> deque;

  for (int r = 0; r < n; r++) {
    while (!deque.empty() && nums[deque.back()] >= nums[r]) {
      deque.pop_back();
    }
    deque.push_back(r);

    while (deque.front() <= r - k) {
      deque.pop_front();
    }
    if (r >= k - 1) {
      cout << nums[deque.front()] << " ";
    }
  }

  return 0;
}
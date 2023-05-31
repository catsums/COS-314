while [ 1 ]; do
   git add -A && git commit -m "Autocommit at $(date)";
   git push -u origin main
   sleep 25;
done
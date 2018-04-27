for i in *; do
  mv "$i" "`echo $i | sed "s/gelid/gelid/"`";
done

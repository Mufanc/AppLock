# Produced by @5ec1cff and modified by @Mufanc

if [ "$(id -u)" != "0" ]; then
    exec su -c sh "$0"
fi

cd /data/local/tmp || exit

function get_services() {
    ss=$(service list | tail -n +2 | tr ':' ' ' | awk '{print $2}')
    for s in $ss ;do
        pid=$(/system/bin/service call "$s" 1599097156 | cut -c16-23)
        if [ -n "$pid" ]; then
            pid=$((16#0$pid))
            echo "$s:pid=$pid,cmdline=$(cat /proc/$pid/cmdline),exec=$(readlink /proc/$pid/exe)"
        fi
    done
}

echo "dumping services ..."
get_services > services.txt 2>/dev/null
echo "dumping props ..."
getprop > props.txt

echo "dumping framework and service jars ..."
# shellcheck disable=SC2046
tar -czvf collect.tgz services.txt props.txt $(echo "$SYSTEMSERVERCLASSPATH" | tr ':' ' ') $(echo "$BOOTCLASSPATH" | tr ':' ' ') 2>/dev/null

mv collect.tgz /sdcard/Download
rm services.txt props.txt
echo "saved in /sdcard/Download/collect.tgz"
